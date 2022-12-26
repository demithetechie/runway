package com.demioshin.runway.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.location.*


class LocationService : Service() {
    // channel ID
    private val channelID = "2"
    // binder
    private var binder: IBinder = MyBinder()

    lateinit var locationCallback: LocationCallback

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var locationRequest: LocationRequest? = null

    val allLocations = mutableListOf<Location>()

    private val currentLocation: Location? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        // create the notification channel
        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations){
                    allLocations.add(location)
                }
            }
        }


        return START_NOT_STICKY

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (locationRequest != null) {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest!!,
                locationCallback,
                Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }


    inner class MyBinder : Binder() {

    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(1500)
            .build()
    }

    // create the notification channel
    private fun createNotificationChannel() {
        val name = "Location"
        val text = "Notifications for location updates"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, name, importance).apply {
            description = text
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}