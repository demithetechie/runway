package com.demioshin.runway.services

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData

class StepsManager(context: Context) : SensorEventListener {

    val steps = MutableLiveData<Int>(0)
    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

//    private var initialSteps = -1

    init {
        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager!!.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val newSteps = sensorEvent!!.values.firstOrNull()

        if (newSteps != null) {
            steps.value = newSteps.toInt()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit
}