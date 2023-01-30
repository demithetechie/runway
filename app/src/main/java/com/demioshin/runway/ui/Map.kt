package com.demioshin.runway.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.demioshin.runway.R
import com.demioshin.runway.data.RunViewModel
import com.demioshin.runway.ui.theme.backgroundColor2
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.demioshin.runway.data.mapState.READY
import com.google.android.gms.maps.MapsInitializer

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapSetup(viewModel: MapViewModel, navController: NavController) {
    val context = LocalContext.current

    Permissions(navigateToSettingsScreen = {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
        )
    }) {
        Map(viewModel = viewModel, navController)
    }
}

@Composable
fun Map(viewModel: MapViewModel, navController: NavController) {
    val context = LocalContext.current

    viewModel.setup(context)

    // setup map components
    MapsInitializer.initialize(context)

    val mapState by remember { viewModel.mapState }

    var isMapReady by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    // observe live run data
    val time by viewModel.runData.value.time.observeAsState()

    // store uiSettings and properties
    val uiSettings by remember { mapState.uiSettings }
    val properties by remember { mapState.properties }

    // store cameraPositionState
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

                mapState.state = READY

                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        viewModel.getCurrentLocation(),
                        17f
                    )
                )
            }
        }
    }

    // register the broadcast receiver
    context.registerReceiver(receiver, intentFilter)

    if (isMapReady) {
        Column {
            Box(
                modifier = Modifier
                    .background(color = backgroundColor2)
                    .fillMaxWidth()
                    .padding(paddingValues = PaddingValues(10.dp))
            ) {
                Column {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_access_time_24),
                            "Time"
                        )
                        Text("Time: $time")
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                ) {

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Button(onClick = {
                        viewModel.startLocationUpdates()
                    }) {
                        Text(text = "Start")
                    }
                    Spacer(modifier = Modifier.width(width = 10.dp))
                    Button(onClick = {
                        viewModel.stopLocationUpdates()

                        navController.navigate("endRun")
                    }) {
                        Text(text = "Stop")
                    }
                }
            }
        }
    }
}

