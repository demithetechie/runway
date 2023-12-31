package com.demioshin.runway.ui

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.demioshin.runway.ui.theme.Shapes
import com.demioshin.runway.ui.theme.backgroundColor
import com.demioshin.runway.ui.theme.buttonColor
import com.demioshin.runway.ui.theme.mainFontColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

// check permissions of the application before loading up app
// taken from https://code.luasoftware.com/tutorials/android/jetpack-compose-request-location-permission
@ExperimentalPermissionsApi
@Composable
fun Permissions(
    navigateToSettingsScreen: () -> Unit,
    content: @Composable() () -> Unit
) {
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
        permissionState.shouldShowRationale ||
                !permissionState.permissionRequested -> {
            if (doNotShowRationale) {
                Text("Feature not available")
            } else {
                Surface(
                    color = backgroundColor,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        Text(
                            "Need to detect current location. Please grant the permission.",
                            color = mainFontColor,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                modifier = Modifier
                                    .height(50.dp),
                                shape = Shapes.medium,
                                colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                                onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                Text("Request permission")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                modifier = Modifier
                                    .height(50.dp),
                                shape = Shapes.medium,
                                colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                                onClick = { doNotShowRationale = true }) {
                                Text("Don't show rationale again")
                            }
                        }
                    }
                }
            }
        }
        else -> {
            Surface(
                color = backgroundColor,
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    Text(
                        "Request location permission denied. " +
                                "Need current location to show nearby places. " +
                                "Please grant access on the Settings screen.",
                        color = mainFontColor,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        modifier = Modifier
                            .width(350.dp)
                            .height(50.dp),
                        shape = Shapes.medium,
                        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                        onClick = navigateToSettingsScreen) {
                        Text("Open Settings")
                    }
                }
            }
        }
    }
}

