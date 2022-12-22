package com.demioshin.runway

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraPositionState
import kotlin.math.roundToInt

class LocationManager(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    var currentLocation = MutableLiveData<LatLng>()
    var locations = mutableListOf<LatLng>()

    var liveLocations = MutableLiveData<List<LatLng>>()

    init {
        currentLocation.value = LatLng(0.0, 0.0)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lat = result.lastLocation?.latitude
            val long = result.lastLocation?.longitude

            Log.d("new lat: ", result.lastLocation?.latitude.toString())
            Log.d("new long: ", result.lastLocation?.longitude.toString())

            val location = LatLng(lat!!, long!!)

            currentLocation.value = location

            locations.add(location)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    // Toast.makeText(context, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    val lat = location.latitude
                    val lon = location.longitude

                    Log.d("lat: ", lat.toString())
                    Log.d("long: ", lon.toString())

                    val loc = MutableLiveData(LatLng(lat, lon))

                    currentLocation = loc
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun trackUserLocation() {
        val locationRequest = LocationRequest.Builder(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(1500)
            .build()

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopTrackingUserLocation() {
        client.removeLocationUpdates(locationCallback)
    }


}