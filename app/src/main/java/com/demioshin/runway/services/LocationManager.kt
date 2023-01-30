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
    // client for getting current location
    private val client = LocationServices.getFusedLocationProviderClient(context)

    // current location
    var currentLocation = MutableLiveData<LatLng>()
    var locations = mutableListOf<LatLng>()
    var liveDistance = MutableLiveData<Int>()

    private var distance = 0

    private val isLocationNull = mutableStateOf(true)

    var liveLocations = MutableLiveData<List<LatLng>>()

    init {
        currentLocation.value = LatLng(0.0, 0.0)
    }

    // location callback for location requests
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lat = result.lastLocation?.latitude
            val long = result.lastLocation?.longitude

//            Log.d("new lat: ", result.lastLocation?.latitude.toString())
//            Log.d("new long: ", result.lastLocation?.longitude.toString())

            val location = LatLng(lat!!, long!!)

            currentLocation.value = location

            locations.add(location)

            liveLocations.value = locations

            if (locations.size > 1) {
                // get new location
                val location1 = Location("prov")
                location1.longitude = location.longitude
                location1.latitude = location.latitude

                // Log.d("Location 1", "lat: ${location1.latitude}, long:  ${location1.longitude}")

                // get previous location
                val location2 = Location("prov1")
                location2.longitude = locations[locations.size - 2].longitude
                location2.latitude = locations[locations.size - 2].latitude

                // Log.d("Location 2", "lat: ${location2.latitude}, long:  ${location2.longitude}")

                // get distance change
                val newDistance = location1.distanceTo(location2)

                // Log.d("distance update", "The distance increment is $newDistance")

                distance += newDistance.toInt()

                // Log.d("distance", "The final distance is $distance")

                liveDistance.postValue(distance)
            }
        }
    }

    // get location when setting up map
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

                    liveDistance.value = distance

                    val broadcastIntent = Intent("CURRENT_LOCATION_FOUND")
                    context.sendBroadcast(broadcastIntent)
                }
            }
    }

    // start location updates
    @SuppressLint("MissingPermission")
    fun trackUserLocation() {
        val locationRequest = LocationRequest.Builder(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(1500)
            .build()

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    // stop location updates
    fun stopTrackingUserLocation() {
        client.removeLocationUpdates(locationCallback)

    }

}