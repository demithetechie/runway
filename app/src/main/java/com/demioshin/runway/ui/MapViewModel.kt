package com.demioshin.runway.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.demioshin.runway.data.MapData
import com.demioshin.runway.services.LocationManager
import com.google.android.gms.maps.model.LatLng


class MapViewModel: ViewModel() {
    var locationManager: LocationManager? = null
    var mapState = mutableStateOf(MapData())
    var location = mutableStateOf(LatLng(0.0, 0.0))

    val receiver = object : BroadcastReceiver() {
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
    }

    fun stopLocationUpdates() {
        locationManager?.stopTrackingUserLocation()
    }

    fun getCurrentLocation(): LatLng {
        return locationManager?.currentLocation?.value!!
    }

}