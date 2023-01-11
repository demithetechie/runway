package com.demioshin.runway.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class LocationManager(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    var currentLocation = MutableLiveData<LatLng>()
    var locations = mutableListOf<LatLng>()
    var distance = MutableLiveData<Float>()

    private val isLocationNull = mutableStateOf(true)

    var liveLocations = MutableLiveData<List<LatLng>>()

    init {
        currentLocation.value = LatLng(0.0, 0.0)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lat = result.lastLocation?.latitude
            val long = result.lastLocation?.longitude

//            Log.d("new lat: ", result.lastLocation?.latitude.toString())
//            Log.d("new long: ", result.lastLocation?.longitude.toString())

            val location = LatLng(lat!!, long!!)

            currentLocation.value = location

            locations.add(location)

            if (locations.size > 1) {
                val location1 = Location("prov")
                location1.longitude = location.longitude
                location1.latitude = location.latitude

                Log.d("Location 1", "lat: ${location1.latitude}, long:  ${location1.longitude}")

                val location2 = Location("prov1")
                location2.longitude = locations[locations.size - 2].longitude
                location2.latitude = locations[locations.size - 2].latitude

                Log.d("Location 2", "lat: ${location2.latitude}, long:  ${location2.longitude}")

                val newDistance = location1.distanceTo(location2)

                Log.d("distance update", "The distance increment is $newDistance")

                val currentDistance = distance.value

                distance.value = distance.value!!.plus(newDistance)

                Log.d("distance", "The final distance is $currentDistance")
            }

            liveLocations.value = locations
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context) {
        client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(context, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    val lat = location.latitude
                    val lon = location.longitude

                    Log.d("current lat: ", lat.toString())
                    Log.d("current long: ", lon.toString())

                    val loc = LatLng(lat, lon)

                    currentLocation.value = loc

                    isLocationNull.value = false

                    distance.value = 0F

                    val broadcastIntent = Intent("CURRENT_LOCATION_FOUND")
                    context.sendBroadcast(broadcastIntent)
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun trackUserLocation() {
        val locationRequest = LocationRequest.Builder(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(1500)
            .build()

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopTrackingUserLocation() {
        client.removeLocationUpdates(locationCallback)

    }

}