package com.demioshin.runway

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class MapViewModel: ViewModel() {
    var locationManager: LocationManager? = null
    var mapState = mutableStateOf(MapData())

    var currentLocation: LatLng? = null

    fun setup(context: Context) {
        locationManager = LocationManager(context)


//        mapState.value.location = mutableStateOf(locationManager.currentLocation)
    }

    fun setupLocationManager(context: Context) {
        locationManager = LocationManager(context)
    }

    fun startLocationUpdates() {
        locationManager?.trackUserLocation()
    }

    fun stopLocationUpdates() {
        locationManager?.stopTrackingUserLocation()
    }

//    fun getCurrentLocation(): LatLng {
//        val lat = locationManager?.currentLocation?.value?.latitude
//        val long = locationManager?.currentLocation?.value?.longitude
//
//        return LatLng(lat ?: 0.0, long ?: 0.0)
//    }

}