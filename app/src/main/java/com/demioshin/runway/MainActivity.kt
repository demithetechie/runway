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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demioshin.runway.data.Run
import com.demioshin.runway.data.RunApplication
import com.demioshin.runway.data.RunViewModel
import com.demioshin.runway.data.RunViewModelFactory
import com.demioshin.runway.ui.*
import com.demioshin.runway.ui.theme.backgroundColor
import com.demioshin.runway.ui.theme.backgroundColor2
import com.demioshin.runway.ui.theme.buttonColor
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
    // map view model
    private val mapViewModel by viewModels<MapViewModel>()

    // run db view model
    private val runViewModel: RunViewModel by viewModels {
        RunViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "start") {
                    composable("map") { MapSetup(mapViewModel, navController) }
                    composable("start") {
                        StartScreen(navController = navController)
                    }
                    composable("runs") { listOfRuns(runViewModel, navController)}
                    composable("endRun") { EndRunScreen(navController, runViewModel, mapViewModel)}
                }
            }
        }
    }
}

