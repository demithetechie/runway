package com.demioshin.runway.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.demioshin.runway.data.MapData
import com.demioshin.runway.data.RunData
import com.demioshin.runway.services.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.demioshin.runway.data.mapState.RUNNING
import com.demioshin.runway.data.mapState.STOPPED
import java.sql.Time


class MapViewModel: ViewModel() {
    var locationManager: LocationManager? = null
    var mapState = mutableStateOf(MapData())
    var runData = mutableStateOf(RunData())
    var location = mutableStateOf(LatLng(0.0, 0.0))

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CURRENT_LOCATION_FOUND") {
                setCurrentLocation()
            }
        }
    }

    fun setCurrentLocation() {
        mapState.value.location = locationManager?.currentLocation?.value!!

        location = mutableStateOf(mapState.value.location)
    }

    fun setup(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction("CURRENT_LOCATION_FOUND")

        locationManager = LocationManager(context)

        locationManager!!.getLocation(context)

        context.registerReceiver(receiver, intentFilter)
    }

    fun startLocationUpdates() {
        locationManager?.trackUserLocation()

        mapState.value.state = RUNNING

        startTimer()
    }

    fun stopLocationUpdates() {
        mapState.value.state = STOPPED

        runData.value.distance = locationManager!!.liveDistance

        locationManager?.stopTrackingUserLocation()
    }

    fun getCurrentLocation(): LatLng {
        return locationManager?.currentLocation?.value!!
    }

    private fun startTimer() {
        var startTime = 0
        Thread {
            while (true) {
                Thread.sleep(1000L)
                startTime++
                val hours: Int = startTime / 3600
                val minutes: Int = startTime / 60
                val seconds: Int = startTime % 60

                Log.d("time", "the time is $startTime seconds")

                runData.value.time.postValue(Time(hours, minutes, seconds))

                if (mapState.value.state == STOPPED) {
                    return@Thread
                }
            }
        }.start()
    }

}