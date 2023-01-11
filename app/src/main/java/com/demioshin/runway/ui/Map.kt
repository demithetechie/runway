package com.demioshin.runway.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Chronometer
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.demioshin.runway.R
import com.demioshin.runway.ui.theme.backgroundColor2
import com.demioshin.runway.util.rememberMapViewWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.demioshin.runway.data.mapState.RUNNING
import com.demioshin.runway.data.mapState.STOPPED
import com.demioshin.runway.data.mapState.READY
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.sql.Time
import kotlin.concurrent.thread

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
        Map(viewModel = viewModel)
    }
}

@Composable
fun Map(viewModel: MapViewModel) {
    val context = LocalContext.current

    viewModel.setup(context)

    val mapView = rememberMapViewWithLifecycle()
    mapView.visibility = View.GONE

    var runText = "Start Run"

    val mapState by remember { viewModel.mapState }

    var isMapReady by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    val userLocation by viewModel.locationManager!!.currentLocation.observeAsState()
    val locations by viewModel.locationManager!!.liveLocations.observeAsState()

    val time by viewModel.runData.value.time.observeAsState()
    val steps by viewModel.stepsManager!!.steps.observeAsState()
    val distance by viewModel.runData.value.distance.observeAsState()

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

                mapState.state = READY

                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(viewModel.getCurrentLocation(), 17f))
            }
        }
    }

    context.registerReceiver(receiver, intentFilter)

    if (isMapReady) {
        Column {
            Box (
                modifier = Modifier.background(color = backgroundColor2).fillMaxWidth().padding(paddingValues = PaddingValues(10.dp))
            ) {
                Column {
                    Row{
                        Icon(painter = painterResource(id = R.drawable.ic_baseline_access_time_24), "Time")
                        Text("Time: $time")
                    }
                    Row{
                        Icon(painter = painterResource(id = R.drawable.ic_baseline_directions_run_24), "Steps")
                        Text("Steps: $steps")
                    }
                    Row{
                        Icon(painter = painterResource(id = R.drawable.ic_baseline_timeline_24), "Distance")
                        Text("Distance: $distance metres")
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth().fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings,
                ) {
                    Circle(
                        center = userLocation!!,
                        radius = 0.3,
                        fillColor = Color.Green,
                        strokeColor = Color.Green,
                        visible = isVisible
                    )
                    if (locations != null) {
                        Polyline(
                            points = locations!!,
                            jointType = JointType.ROUND,
                            color = Color.Green,
                        )
                    }
                }
                Row (
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
                    }) {
                        Text(text = "Stop")
                    }
                }
            }
        }
    }
}

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