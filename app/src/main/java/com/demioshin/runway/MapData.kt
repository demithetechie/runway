package com.demioshin.runway

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings

data class MapData(
    val state: String = "START",
    val uiSettings: MutableState<MapUiSettings> = mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)),
    val properties: MutableState<MapProperties> = mutableStateOf(
        MapProperties(mapType = MapType.NORMAL)),
    var location: LatLng = LatLng(0.0, 0.0)
    ) {

    fun isReady(): Boolean {
        return state == "READY"
    }
}