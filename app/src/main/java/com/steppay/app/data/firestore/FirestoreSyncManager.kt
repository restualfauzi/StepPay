package com.steppay.app.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.steppay.app.data.local.entity.ActivitySessionEntity
import com.steppay.app.data.local.entity.CoinTransactionEntity
import com.steppay.app.data.local.entity.UserProfileEntity
import com.steppay.app.data.local.entity.WithdrawalEntity

object FirestoreSyncManager {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var userListenerRegistration: ListenerRegistration? = null

    private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitTask(): T? =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result ->
                if (continuation.isActive) continuation.resumeWith(Result.success(result))
            }
            addOnFailureListener {
                if (continuation.isActive) continuation.resumeWith(Result.success(null))
            }
        }

    fun listenToUserProfile(email: String, onProfileUpdated: (UserProfileEntity) -> Unit) {
        if (email.isBlank()) return
        userListenerRegistration?.remove()
        userListenerRegistration = firestore.collection("users")
            .document(email)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val name = snapshot.getString("name") ?: "User"
                val photoUrl = snapshot.getString("photoUrl") ?: ""
                val memberId = snapshot.getString("memberId") ?: "SP101"
                val badge = snapshot.getString("badge") ?: "Member"
                val dailyTargetSteps = (snapshot.getLong("dailyTargetSteps") ?: 5000L).toInt()
                val todaySteps = (snapshot.getLong("todaySteps") ?: 0L).toInt()
                val totalCoins = (snapshot.getLong("totalCoins") ?: 0L).toInt()
                val rupiahBalance = (snapshot.getLong("rupiahBalance") ?: 0L).toInt()
                val paymentMethod = snapshot.getString("paymentMethod") ?: "PayPal"
                val paymentAccount = snapshot.getString("paymentAccount") ?: email

                val updatedProfile = UserProfileEntity(
                    id = 1,
                    name = name,
                    email = email,
                    photoUrl = photoUrl,
                    memberId = memberId,
                    badge = badge,
                    dailyTargetSteps = dailyTargetSteps,
                    todaySteps = todaySteps,
                    totalCoins = totalCoins,
                    rupiahBalance = rupiahBalance,
                    paymentMethod = paymentMethod,
                    paymentAccount = paymentAccount,
                    checksum = ""
                )
                onProfileUpdated(updatedProfile)
            }
    }

    suspend fun fetchUserProfile(email: String): UserProfileEntity? {
        if (email.isBlank()) return null
        return try {
            val doc = firestore.collection("users").document(email).get().awaitTask()
            if (doc != null && doc.exists()) {
                UserProfileEntity(
                    id = 1,
                    name = doc.getString("name") ?: "User",
                    email = doc.getString("email") ?: email,
                    photoUrl = doc.getString("photoUrl") ?: "",
                    memberId = doc.getString("memberId") ?: "SP${System.currentTimeMillis().toString().takeLast(8)}",
                    badge = doc.getString("badge") ?: "Member",
                    dailyTargetSteps = (doc.getLong("dailyTargetSteps") ?: 5000L).toInt(),
                    todaySteps = (doc.getLong("todaySteps") ?: 0L).toInt(),
                    totalCoins = (doc.getLong("totalCoins") ?: 0L).toInt(),
                    rupiahBalance = (doc.getLong("rupiahBalance") ?: 0L).toInt(),
                    paymentMethod = doc.getString("paymentMethod") ?: "PayPal",
                    paymentAccount = doc.getString("paymentAccount") ?: email,
                    checksum = ""
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun fetchTransactions(email: String): List<CoinTransactionEntity> {
        if (email.isBlank()) return emptyList()
        return try {
            val snapshot = firestore.collection("coin_transactions")
                .whereEqualTo("userId", email)
                .get()
                .awaitTask()
            snapshot?.documents?.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                val subtitle = doc.getString("subtitle") ?: ""
                val amountCoins = (doc.getLong("amountCoins") ?: 0L).toInt()
                val isPositive = doc.getBoolean("isPositive") ?: true
                val category = doc.getString("category") ?: "BONUS"
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                CoinTransactionEntity(
                    title = title,
                    subtitle = subtitle,
                    amountCoins = amountCoins,
                    isPositive = isPositive,
                    category = category,
                    timestamp = timestamp
                )
            } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun syncUserProfile(profile: UserProfileEntity) {
        val userId = profile.email.ifBlank { profile.memberId }
        if (userId.isBlank()) return

        val userMap = hashMapOf(
            "name" to profile.name,
            "email" to profile.email,
            "photoUrl" to profile.photoUrl,
            "memberId" to profile.memberId,
            "badge" to profile.badge,
            "dailyTargetSteps" to profile.dailyTargetSteps,
            "todaySteps" to profile.todaySteps,
            "totalCoins" to profile.totalCoins,
            "rupiahBalance" to profile.rupiahBalance,
            "paymentMethod" to profile.paymentMethod,
            "paymentAccount" to profile.paymentAccount,
            "lastUpdated" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userId)
            .set(userMap, SetOptions.merge())

        // Sync to live leaderboard collection
        val leaderboardMap = hashMapOf(
            "userId" to userId,
            "name" to profile.name,
            "photoUrl" to profile.photoUrl,
            "steps" to profile.todaySteps,
            "coins" to profile.totalCoins,
            "lastUpdated" to System.currentTimeMillis()
        )

        firestore.collection("leaderboard")
            .document(userId)
            .set(leaderboardMap, SetOptions.merge())
    }

    fun syncActivitySession(session: ActivitySessionEntity, profile: UserProfileEntity?) {
        val userId = profile?.email?.ifBlank { profile.memberId } ?: "unknown_user"

        val sessionMap = hashMapOf(
            "userId" to userId,
            "userName" to (profile?.name ?: "User"),
            "type" to session.type,
            "steps" to session.steps,
            "distanceKm" to session.distanceKm,
            "durationSeconds" to session.durationSeconds,
            "calories" to session.calories,
            "coinsEarned" to session.coinsEarned,
            "routeJson" to session.routeJson,
            "timestamp" to session.timestamp
        )

        firestore.collection("activity_sessions")
            .document("session_${session.timestamp}")
            .set(sessionMap, SetOptions.merge())
    }

    fun syncTransaction(transaction: CoinTransactionEntity, profile: UserProfileEntity?) {
        val userId = profile?.email?.ifBlank { profile.memberId } ?: "unknown_user"

        val txMap = hashMapOf(
            "userId" to userId,
            "title" to transaction.title,
            "subtitle" to transaction.subtitle,
            "amountCoins" to transaction.amountCoins,
            "isPositive" to transaction.isPositive,
            "category" to transaction.category,
            "timestamp" to transaction.timestamp
        )

        firestore.collection("coin_transactions")
            .document("tx_${transaction.timestamp}")
            .set(txMap, SetOptions.merge())
    }

    fun syncWithdrawal(withdrawal: WithdrawalEntity, profile: UserProfileEntity?) {
        val userId = profile?.email?.ifBlank { profile.memberId } ?: "unknown_user"

        val withdrawalMap = hashMapOf(
            "userId" to userId,
            "userName" to (profile?.name ?: "User"),
            "method" to withdrawal.method,
            "accountNumber" to withdrawal.accountNumber,
            "amountRupiah" to withdrawal.amountRupiah,
            "coinCost" to withdrawal.coinCost,
            "status" to withdrawal.status,
            "transactionNumber" to withdrawal.transactionNumber,
            "timestamp" to withdrawal.timestamp
        )

        firestore.collection("withdrawals")
            .document(withdrawal.transactionNumber)
            .set(withdrawalMap, SetOptions.merge())
    }
}
