package com.steppay.app.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.steppay.app.data.datastore.UserPreferences
import com.steppay.app.data.repository.StepPayRepository
import com.steppay.app.domain.payout.PayoutRepository
import com.steppay.app.domain.reward.RewardEngine
import com.steppay.app.service.ActivityTrackingService
import com.steppay.app.service.TrackingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StepPayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StepPayRepository(application)
    private val userPreferences = UserPreferences(application)
    val payoutRepository = PayoutRepository()

    val userProfile = repository.userProfile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val allTransactions = repository.allTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allWithdrawals = repository.allWithdrawals.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allSessions = repository.allSessions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val selectedCountry = userPreferences.selectedCountry.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "US"
    )

    val distanceUnit = userPreferences.distanceUnit.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "KM"
    )

    val isOnboardingCompleted = userPreferences.isOnboardingCompleted.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val trackingState: StateFlow<TrackingState> = ActivityTrackingService.trackingState

    private val _withdrawalMessage = MutableStateFlow<String?>(null)
    val withdrawalMessage: StateFlow<String?> = _withdrawalMessage.asStateFlow()

    fun setSelectedCountry(countryCode: String) {
        viewModelScope.launch {
            userPreferences.setSelectedCountry(countryCode)
        }
    }

    fun setDistanceUnit(unitCode: String) {
        viewModelScope.launch {
            userPreferences.setDistanceUnit(unitCode)
        }
    }

    fun awardAdBonusCoins(coins: Int = 50) {
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                repository.updateDailyTarget(profile.dailyTargetSteps) // touch
            }
        }
    }

    fun startTracking(type: String = "Walking") {
        val intent = Intent(getApplication(), ActivityTrackingService::class.java).apply {
            action = ActivityTrackingService.ACTION_START
            putExtra(ActivityTrackingService.EXTRA_TYPE, type)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            getApplication<Application>().startForegroundService(intent)
        } else {
            getApplication<Application>().startService(intent)
        }
    }

    fun pauseTracking() {
        val intent = Intent(getApplication(), ActivityTrackingService::class.java).apply {
            action = ActivityTrackingService.ACTION_PAUSE
        }
        getApplication<Application>().startService(intent)
    }

    fun resumeTracking() {
        val intent = Intent(getApplication(), ActivityTrackingService::class.java).apply {
            action = ActivityTrackingService.ACTION_RESUME
        }
        getApplication<Application>().startService(intent)
    }

    fun finishAndSaveActivity(onSaved: () -> Unit) {
        val currentState = trackingState.value
        viewModelScope.launch {
            if (currentState.steps > 0 || currentState.durationSeconds > 5) {
                val coins = RewardEngine.calculateActivityCoins(currentState.steps)
                val routeJson = currentState.routePoints.joinToString(";") { "${it.first},${it.second}" }

                repository.saveActivitySession(
                    type = currentState.activityType,
                    steps = currentState.steps,
                    distanceKm = currentState.distanceKm,
                    durationSeconds = currentState.durationSeconds,
                    calories = currentState.calories,
                    coinsEarned = coins,
                    routeJson = routeJson
                )
            }

            val intent = Intent(getApplication(), ActivityTrackingService::class.java).apply {
                action = ActivityTrackingService.ACTION_STOP
            }
            getApplication<Application>().startService(intent)
            onSaved()
        }
    }

    fun processWithdrawal(method: String, accountNumber: String, amountRupiah: Int, coinCost: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val success = repository.processWithdrawal(method, accountNumber, amountRupiah, coinCost)
            if (success) {
                val displayDollars = if (amountRupiah >= 1000) amountRupiah / 1000 else amountRupiah
                _withdrawalMessage.value = "Payout request of $$displayDollars.00 submitted!\n\n🛡️ Anti-Fraud Requirement: Please log in daily for 7 consecutive days to complete verification & release your PayPal cash! Progress: Day 1/7."
                onSuccess()
            } else {
                _withdrawalMessage.value = "Insufficient coins to make this payout."
            }
        }
    }

    fun clearWithdrawalMessage() {
        _withdrawalMessage.value = null
    }

    fun claimBonusCoins(coins: Int, title: String, subtitle: String, onClaimed: () -> Unit = {}) {
        viewModelScope.launch {
            repository.claimBonusCoins(coins, title, subtitle)
            onClaimed()
        }
    }

    fun updateDailyTarget(newTarget: Int) {
        viewModelScope.launch {
            repository.updateDailyTarget(newTarget)
        }
    }

    fun updateProfileName(newName: String) {
        viewModelScope.launch {
            repository.updateProfileName(newName)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setOnboardingCompleted(true)
        }
    }

    fun signInWithGoogle(name: String, email: String, photoUrl: String = "") {
        viewModelScope.launch {
            repository.updateGoogleProfile(name, email, photoUrl)
            userPreferences.setOnboardingCompleted(true)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            userPreferences.setOnboardingCompleted(false)
        }
    }

    fun parseRouteJson(routeJson: String): List<Pair<Double, Double>> {
        if (routeJson.isBlank()) return emptyList()
        return routeJson.split(";").mapNotNull { point ->
            val parts = point.split(",")
            if (parts.size == 2) {
                val lat = parts[0].toDoubleOrNull()
                val lng = parts[1].toDoubleOrNull()
                if (lat != null && lng != null) Pair(lat, lng) else null
            } else null
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                StepPayViewModel(application)
            }
        }
    }
}