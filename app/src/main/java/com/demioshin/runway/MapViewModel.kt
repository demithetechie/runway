package com.demioshin.runway

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
    var userCurrentLng: MutableState<Double> = mutableStateOf(-1.158109)
    var userCurrentLat: MutableState<Double> = mutableStateOf(52.954784)

    fun updateCurrentLocation(location: Location?) {
        userCurrentLat = mutableStateOf(location?.latitude ?: 0.0)
        userCurrentLat = mutableStateOf(location?.longitude ?: 0.0)

    }

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(context, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    val lat = location.latitude
                    val lon = location.longitude
                    Log.d("lat", lat.toString())
                    Log.d("long", lon.toString())
                    updateCurrentLocation(location)

                }

            }
    }

}