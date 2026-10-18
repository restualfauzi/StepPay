package com.steppay.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.steppay.app.MainActivity
import com.steppay.app.R
import com.steppay.app.StepPayApplication
import com.steppay.app.domain.reward.RewardEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackingState(
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val steps: Int = 0,
    val distanceKm: Double = 0.0,
    val durationSeconds: Long = 0,
    val calories: Int = 0,
    val coinsEarned: Int = 0,
    val routePoints: List<Pair<Double, Double>> = emptyList(), // Pair(latitude, longitude)
    val activityType: String = "Walking" // "Walking" or "Running"
)

class ActivityTrackingService : Service() {

    private val binder = LocalBinder()
    private lateinit var stepSensorManager: StepSensorManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null
    private var lastLocation: Location? = null

    inner class LocalBinder : Binder() {
        fun getService(): ActivityTrackingService = this@ActivityTrackingService
    }

    override fun onCreate() {
        super.onCreate()
        stepSensorManager = StepSensorManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupLocationCallback()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (_trackingState.value.isPaused || !_trackingState.value.isTracking) return

                for (location in locationResult.locations) {
                    if (location.accuracy > 30) continue // Skip inaccurate GPS points

                    val lastLoc = lastLocation
                    if (lastLoc != null) {
                        val addedDistanceMeters = lastLoc.distanceTo(location)
                        if (addedDistanceMeters > 0.5) { // Threshold 0.5 meters
                            _trackingState.update { currentState ->
                                val newDist = currentState.distanceKm + (addedDistanceMeters / 1000.0)
                                val roundedDist = (newDist * 100).toInt() / 100.0
                                val newRoute = currentState.routePoints + Pair(location.latitude, location.longitude)
                                currentState.copy(
                                    distanceKm = roundedDist,
                                    routePoints = newRoute
                                )
                            }
                        }
                    } else {
                        // First point
                        _trackingState.update { currentState ->
                            currentState.copy(
                                routePoints = currentState.routePoints + Pair(location.latitude, location.longitude)
                            )
                        }
                    }
                    lastLocation = location
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking(intent.getStringExtra(EXTRA_TYPE) ?: "Walking")
            ACTION_PAUSE -> pauseTracking()
            ACTION_RESUME -> resumeTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    fun startTracking(activityType: String = "Walking") {
        _trackingState.value = TrackingState(
            isTracking = true,
            isPaused = false,
            activityType = activityType
        )

        startForeground(NOTIFICATION_ID, buildNotification())
        stepSensorManager.startListening()
        startLocationUpdates()
        startTimer()

        // Sync step sensor changes to tracking state
        scope.launch {
            stepSensorManager.stepCount.collect { steps ->
                if (_trackingState.value.isTracking && !_trackingState.value.isPaused) {
                    val calories = RewardEngine.estimateCalories(steps)
                    val coins = RewardEngine.calculateActivityCoins(steps)
                    val calculatedDist = if (_trackingState.value.distanceKm > 0.05) {
                        _trackingState.value.distanceKm
                    } else {
                        RewardEngine.estimateDistanceKm(steps)
                    }

                    _trackingState.update {
                        it.copy(
                            steps = steps,
                            calories = calories,
                            coinsEarned = coins,
                            distanceKm = calculatedDist
                        )
                    }
                    updateNotification()
                }
            }
        }
    }

    fun pauseTracking() {
        _trackingState.update { it.copy(isPaused = true) }
        timerJob?.cancel()
        stepSensorManager.stopListening()
        stopLocationUpdates()
        updateNotification()
    }

    fun resumeTracking() {
        _trackingState.update { it.copy(isPaused = false) }
        stepSensorManager.startListening()
        startLocationUpdates()
        startTimer()
        updateNotification()
    }

    fun stopTracking() {
        timerJob?.cancel()
        stepSensorManager.stopListening()
        stopLocationUpdates()

        _trackingState.update { it.copy(isTracking = false, isPaused = false) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (_trackingState.value.isTracking && !_trackingState.value.isPaused) {
                delay(1000)
                _trackingState.update { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(2000)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val state = _trackingState.value
        val minutes = state.durationSeconds / 60
        val seconds = state.durationSeconds % 60
        val timeStr = String.format("%02d:%02d", minutes, seconds)

        return NotificationCompat.Builder(this, StepPayApplication.TRACKING_CHANNEL_ID)
            .setContentTitle("StepPay — ${state.activityType} Active")
            .setContentText("${state.steps} steps • ${state.distanceKm} km • $timeStr")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    override fun onBind(intent: Intent?): IBinder = binder

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_TYPE = "EXTRA_TYPE"

        private val _trackingState = MutableStateFlow(TrackingState())
        val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()
    }
}