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
import com.demioshin.runway.screens.MapViewModel
import com.demioshin.runway.util.rememberMapViewWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.maps.android.compose.*
import kotlinx.coroutines.*


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MapViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                 Map(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BetterMap(viewModel: MapViewModel) {
    val context = LocalContext.current

    viewModel.setup(context)

    val mapView = rememberMapViewWithLifecycle()
    mapView.visibility = View.GONE

    val mapState by remember { viewModel.mapState }

    var isMapReady by remember { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }

    val userLocation by viewModel.locationManager!!.currentLocation.observeAsState()

    val locations by viewModel.locationManager!!.liveLocations.observeAsState()

    val uiSettings by remember { mapState.uiSettings }
    val properties by remember { mapState.properties }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction("CURRENT_LOCATION_FOUND")

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CURRENT_LOCATION_FOUND") {
                isMapReady = true
                isVisible = true

                cameraPositionState.move(CameraUpdateFactory.newLatLng(viewModel.getCurrentLocation()))
            }
        }
    }

    context.registerReceiver(receiver, intentFilter)

//    Box{
//        AndroidView({ mapView }) { mapView ->
//            CoroutineScope(Dispatchers.Main).launch {
//                mapView.getMapAsync {
//                    map = it
//                    isMapReady.value = true
//
//                    map!!.uiSettings.isZoomControlsEnabled = true
//                    map!!.mapType = MAP_TYPE_NORMAL
//                }
//            }
//        }
//    }
//    Row {
//        Button(onClick = {
//            viewModel.startLocationUpdates()
//        }) {
//            Text(text = "Start Run")
//        }
//        Spacer(modifier = Modifier.width(width = 10.dp))
//        Button(onClick = {
//            viewModel.getCurrentLocation()
//
//        }) {
//            Text(text = "End Run")
//        }
//    }


    if (isMapReady) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings,
            ) {
                Circle(
                    center = userLocation!!,
                    radius = 10.0,
                    fillColor = Green,
                    strokeColor = Green,
                    visible = isVisible
                )
                if (locations != null) {
                    Polyline(
                        points = locations!!,
                        jointType = JointType.ROUND,
                        color = Green,
                    )
                }
                }
            }
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
                ) {
            Button(onClick = {
                viewModel.startLocationUpdates()
            }) {
                Text(text = "Start Run")
            }
            Spacer(modifier = Modifier.width(width = 10.dp))
            Button(onClick = {
                viewModel.getCurrentLocation()

            }) {
                Text(text = "Locate")
            }
        }
        }
    }


//fun addCircle(map: GoogleMap, location: LatLng) {
//    map.addCircle(CircleOptions()
//        .radius(1000.0)
//        .center(location)
//    )
//}


@ExperimentalPermissionsApi
@Composable
fun RequireLocationPermission(
    navigateToSettingsScreen: () -> Unit,
    content: @Composable() () -> Unit
) {
    // Track if the user doesn't want to see the rationale any more.
    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

    // Permission state
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    when {
        permissionState.allPermissionsGranted -> {
            content()
        }
        // If the user denied the permission but a rationale should be shown, or the user sees
        // the permission for the first time, explain why the feature is needed by the app and allow
        // the user to be presented with the permission again or to not see the rationale any more.
        permissionState.shouldShowRationale ||
                !permissionState.permissionRequested -> {
            if (doNotShowRationale) {
                Text("Feature not available")
            } else {
                Column {
                    Text("Need to detect current location. Please grant the permission.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                            Text("Request permission")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { doNotShowRationale = true }) {
                            Text("Don't show rationale again")
                        }
                    }
                }
            }
        }
        // If the criteria above hasn't been met, the user denied the permission. Let's present
        // the user with a FAQ in case they want to know more and send them to the Settings screen
        // to enable it the future there if they want to.
        else -> {
            Column {
                Text(
                    "Request location permission denied. " +
                            "Need current location to show nearby places. " +
                            "Please grant access on the Settings screen."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = navigateToSettingsScreen) {
                    Text("Open Settings")
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Map(viewModel: MapViewModel) {
    val context = LocalContext.current

    RequireLocationPermission(navigateToSettingsScreen = {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
        )
    }) {
        BetterMap(viewModel = viewModel)
    }
}