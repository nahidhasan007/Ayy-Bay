package com.ayybay.app.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

private const val READ_TIMEOUT_MILLIS = 5_000L

/** Wraps the hardware step counter, which reports cumulative steps since the last reboot. */
class StepSensor(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    val isAvailable: Boolean get() = sensor != null

    /** Current counter value, or null if there is no sensor or it didn't answer in time. */
    suspend fun readOnce(): Long? {
        val stepSensor = sensor ?: return null
        return withTimeoutOrNull(READ_TIMEOUT_MILLIS) {
            suspendCancellableCoroutine { cont ->
                val listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        sensorManager.unregisterListener(this)
                        if (cont.isActive) cont.resume(event.values[0].toLong())
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
                }
                val registered = sensorManager.registerListener(
                    listener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL
                )
                if (!registered && cont.isActive) cont.resume(null)
                cont.invokeOnCancellation { sensorManager.unregisterListener(listener) }
            }
        }
    }

    /** Emits the counter on every change for as long as it is collected. */
    fun liveCounts(): Flow<Long> = callbackFlow {
        val stepSensor = sensor
        if (stepSensor == null) {
            close()
            return@callbackFlow
        }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(event.values[0].toLong())
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
