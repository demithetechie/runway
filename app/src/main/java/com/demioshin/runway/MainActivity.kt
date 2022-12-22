package com.demioshin.runway

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import com.demioshin.runway.ui.theme.RunwayTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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

@Composable
fun BetterMap(viewModel: MapViewModel) {
    val context = LocalContext.current

    viewModel.setupLocationManager(context)

    val isVisible = true

    val mapState by remember { viewModel.mapState }

//    val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == "LOCATION_FOUND") {
//
//            }
//        }
//    }

    val userLocation by viewModel.locationManager!!.currentLocation.observeAsState()

    var uiSettings by remember { mapState.uiSettings }
    val properties by remember { mapState.properties }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation!!, 10f)
    }

    viewModel.startLocationUpdates()

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings
        ) {
            Circle(
                center = userLocation!!,
                radius = 15.0,
                fillColor = Green,
                strokeColor = Green,
                visible = isVisible
            )
        }
        Switch(
            checked = uiSettings.zoomControlsEnabled,
            onCheckedChange = {
                uiSettings = uiSettings.copy(zoomControlsEnabled = it)
            }
        )

    }

}
