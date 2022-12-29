package com.demioshin.runway

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demioshin.runway.ui.Map
import com.demioshin.runway.ui.MapSetup
import com.demioshin.runway.ui.MapViewModel
import com.demioshin.runway.ui.StartScreen
import com.demioshin.runway.util.rememberMapViewWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.maps.android.compose.*
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MapViewModel>()

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "map") {
                    composable("map") { Map(viewModel) }
                    composable("start") {
                        StartScreen(navController = navController)
                    }
                }
            }
        }

//    @Composable
//    fun NewMap(onMapReadyCallback: OnMapReadyCallback) {
//        AndroidView(
//            modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
//            factory = {
//                mapView
//            },
//            update = {
//                mapView.getMapAsync(onMapReadyCallback)
//            }
//        )
//    }
//
//    override fun onMapReady(map: GoogleMap) {
//        map.addCircle(CircleOptions().radius(300.0).center(LatLng(51.1, -1.0)))
//        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(51.1, -1.0)))
//    }
    }
}


