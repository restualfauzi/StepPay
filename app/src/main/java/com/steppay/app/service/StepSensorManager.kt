package com.steppay.app.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StepSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var stepDetectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    private var initialSensorSteps = -1
    private var isSimulating = false
    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startListening() {
        _stepCount.value = 0
        initialSensorSteps = -1

        val registered = when {
            stepCounterSensor != null -> {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
            }
            stepDetectorSensor != null -> {
                sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
            }
            else -> false
        }

        if (!registered) {
            // Fallback simulation for emulators or devices without physical step sensors
            isSimulating = true
            startSimulation()
        }
    }

    fun stopListening() {
        if (!isSimulating) {
            sensorManager.unregisterListener(this)
        } else {
            simulationJob?.cancel()
            simulationJob = null
            isSimulating = false
        }
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            while (isActive) {
                delay(1200) // Simulate ~1-2 steps per 1.2s
                _stepCount.value += (1..3).random()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSensorSteps = event.values[0].toInt()
            if (initialSensorSteps < 0) {
                initialSensorSteps = totalSensorSteps
            }
            _stepCount.value = (totalSensorSteps - initialSensorSteps).coerceAtLeast(0)
        } else if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                _stepCount.value += 1
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}