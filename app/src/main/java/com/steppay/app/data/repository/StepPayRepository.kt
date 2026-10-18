package com.steppay.app.data.repository

import android.content.Context
import com.steppay.app.data.firestore.FirestoreSyncManager
import com.steppay.app.data.local.StepPayDatabase
import com.steppay.app.data.local.entity.ActivitySessionEntity
import com.steppay.app.data.local.entity.CoinTransactionEntity
import com.steppay.app.data.local.entity.UserProfileEntity
import com.steppay.app.data.local.entity.WithdrawalEntity
import com.steppay.app.data.security.SecurityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class StepPayRepository(context: Context) {

    private val db = StepPayDatabase.getDatabase(context)
    private val activityDao = db.activityDao()
    private val transactionDao = db.transactionDao()
    private val withdrawalDao = db.withdrawalDao()
    private val userProfileDao = db.userProfileDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
            val profile = userProfileDao.getUserProfile().first()
            if (profile != null && profile.email.isNotBlank()) {
                FirestoreSyncManager.listenToUserProfile(profile.email) { updatedFromFirestore ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val newChecksum = SecurityManager.calculateCoinChecksum(updatedFromFirestore.memberId, updatedFromFirestore.totalCoins, updatedFromFirestore.todaySteps)
                        userProfileDao.insertOrUpdateProfile(updatedFromFirestore.copy(checksum = newChecksum))
                    }
                }
            }
        }
    }

    private suspend fun syncAndSignProfile() {
        val profile = userProfileDao.getUserProfile().first() ?: return
        val newChecksum = SecurityManager.calculateCoinChecksum(profile.memberId, profile.totalCoins, profile.todaySteps)
        val signed = profile.copy(checksum = newChecksum)
        userProfileDao.insertOrUpdateProfile(signed)
        FirestoreSyncManager.syncUserProfile(signed)
    }

    private suspend fun seedInitialDataIfEmpty() {
        val currentProfile = userProfileDao.getUserProfile().first()
        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val email = firebaseUser?.email ?: currentProfile?.email ?: ""

        if (currentProfile == null || (currentProfile.totalCoins == 0 && email.isNotBlank())) {
            if (email.isNotBlank()) {
                val remoteProfile = FirestoreSyncManager.fetchUserProfile(email)
                if (remoteProfile != null) {
                    val newChecksum = SecurityManager.calculateCoinChecksum(remoteProfile.memberId, remoteProfile.totalCoins, remoteProfile.todaySteps)
                    val signed = remoteProfile.copy(checksum = newChecksum)
                    userProfileDao.insertOrUpdateProfile(signed)

                    val txs = FirestoreSyncManager.fetchTransactions(email)
                    txs.forEach { transactionDao.insertTransaction(it) }

                    FirestoreSyncManager.syncUserProfile(signed)
                    return
                }
            }

            if (currentProfile == null) {
                val memId = "SP${System.currentTimeMillis().toString().takeLast(8)}"
                val initialProfile = UserProfileEntity(
                    id = 1,
                    name = firebaseUser?.displayName ?: "New User",
                    email = email,
                    photoUrl = firebaseUser?.photoUrl?.toString() ?: "",
                    memberId = memId,
                    badge = "Member",
                    dailyTargetSteps = 5000,
                    todaySteps = 0,
                    totalCoins = 0,
                    rupiahBalance = 0,
                    paymentMethod = "PayPal",
                    paymentAccount = email.ifBlank { "user@paypal.com" },
                    checksum = SecurityManager.calculateCoinChecksum(memId, 0, 0)
                )
                userProfileDao.insertOrUpdateProfile(initialProfile)
                if (email.isNotBlank()) {
                    FirestoreSyncManager.syncUserProfile(initialProfile)
                }
            } else {
                syncAndSignProfile()
            }
        } else {
            syncAndSignProfile()
        }
    }

    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()
    val allTransactions: Flow<List<CoinTransactionEntity>> = transactionDao.getAllTransactions()
    val allWithdrawals: Flow<List<WithdrawalEntity>> = withdrawalDao.getAllWithdrawals()
    val allSessions: Flow<List<ActivitySessionEntity>> = activityDao.getAllSessions()

    suspend fun saveActivitySession(
        type: String,
        steps: Int,
        distanceKm: Double,
        durationSeconds: Long,
        calories: Int,
        coinsEarned: Int,
        routeJson: String
    ): Long {
        val session = ActivitySessionEntity(
            type = type,
            steps = steps,
            distanceKm = distanceKm,
            durationSeconds = durationSeconds,
            calories = calories,
            coinsEarned = coinsEarned,
            routeJson = routeJson
        )
        val id = activityDao.insertSession(session)

        // Add steps and coins to profile
        userProfileDao.addTodaySteps(steps)
        if (coinsEarned > 0) {
            userProfileDao.addCoins(coinsEarned)
            val formatDistance = String.format(Locale.getDefault(), "%.2f km", distanceKm)
            val formatMinutes = "${durationSeconds / 60} min"
            val tx = CoinTransactionEntity(
                title = type,
                subtitle = "$formatDistance • $formatMinutes",
                amountCoins = coinsEarned,
                isPositive = true,
                category = "ACTIVITY"
            )
            transactionDao.insertTransaction(tx)

            val profile = userProfileDao.getUserProfile().first()
            FirestoreSyncManager.syncTransaction(tx, profile)
        }

        val updatedProfile = userProfileDao.getUserProfile().first()
        if (updatedProfile != null) {
            syncAndSignProfile()
            FirestoreSyncManager.syncActivitySession(session, updatedProfile)
        }
        return id
    }

    suspend fun claimBonusCoins(coins: Int, title: String, subtitle: String) {
        userProfileDao.addCoins(coins)
        val tx = CoinTransactionEntity(
            title = title,
            subtitle = subtitle,
            amountCoins = coins,
            isPositive = true,
            category = "BONUS"
        )
        transactionDao.insertTransaction(tx)

        val updatedProfile = userProfileDao.getUserProfile().first()
        if (updatedProfile != null) {
            syncAndSignProfile()
            FirestoreSyncManager.syncTransaction(tx, updatedProfile)
        }
    }

    suspend fun processWithdrawal(
        method: String,
        accountNumber: String,
        amountRupiah: Int,
        coinCost: Int
    ): Boolean {
        val currentProfile = userProfileDao.getUserProfile().first() ?: return false
        if (currentProfile.totalCoins < coinCost) {
            return false
        }

        userProfileDao.deductCoins(coinCost)

        val trxNum = "TRX-${System.currentTimeMillis().toString().takeLast(8)}"
        val withdrawal = WithdrawalEntity(
            method = method,
            accountNumber = accountNumber,
            amountRupiah = amountRupiah,
            coinCost = coinCost,
            status = "Pending (7-Day Login: 1/7)",
            transactionNumber = trxNum
        )
        withdrawalDao.insertWithdrawal(withdrawal)

        val tx = CoinTransactionEntity(
            title = "Payout $method",
            subtitle = "$$amountRupiah.00",
            amountCoins = coinCost,
            isPositive = false,
            category = "WITHDRAWAL"
        )
        transactionDao.insertTransaction(tx)

        val updatedProfile = userProfileDao.getUserProfile().first()
        if (updatedProfile != null) {
            syncAndSignProfile()
            FirestoreSyncManager.syncWithdrawal(withdrawal, updatedProfile)
            FirestoreSyncManager.syncTransaction(tx, updatedProfile)
        }

        return true
    }

    suspend fun updateDailyTarget(newTarget: Int) {
        userProfileDao.updateDailyTarget(newTarget)
        syncAndSignProfile()
    }

    suspend fun updateProfileName(newName: String) {
        val profile = userProfileDao.getUserProfile().first() ?: return
        val updated = profile.copy(name = newName)
        userProfileDao.insertOrUpdateProfile(updated)
        syncAndSignProfile()
    }

    suspend fun updateGoogleProfile(name: String, email: String, photoUrl: String) {
        if (email.isNotBlank()) {
            val remoteProfile = FirestoreSyncManager.fetchUserProfile(email)
            if (remoteProfile != null) {
                val restoredProfile = remoteProfile.copy(
                    name = if (name.isNotBlank()) name else remoteProfile.name,
                    photoUrl = if (photoUrl.isNotBlank()) photoUrl else remoteProfile.photoUrl
                )
                val newChecksum = SecurityManager.calculateCoinChecksum(restoredProfile.memberId, restoredProfile.totalCoins, restoredProfile.todaySteps)
                val signedProfile = restoredProfile.copy(checksum = newChecksum)

                userProfileDao.insertOrUpdateProfile(signedProfile)

                val txs = FirestoreSyncManager.fetchTransactions(email)
                txs.forEach { transactionDao.insertTransaction(it) }

                FirestoreSyncManager.syncUserProfile(signedProfile)
                return
            }
        }

        val current = userProfileDao.getUserProfile().first()
        if (current != null) {
            userProfileDao.updateGoogleProfile(name, email, photoUrl)
            syncAndSignProfile()
        } else {
            val memId = "SP${System.currentTimeMillis().toString().takeLast(8)}"
            val newProfile = UserProfileEntity(
                id = 1,
                name = name,
                email = email,
                photoUrl = photoUrl,
                memberId = memId,
                paymentAccount = email,
                checksum = SecurityManager.calculateCoinChecksum(memId, 0, 0)
            )
            userProfileDao.insertOrUpdateProfile(newProfile)
            FirestoreSyncManager.syncUserProfile(newProfile)
        }
    }
}
