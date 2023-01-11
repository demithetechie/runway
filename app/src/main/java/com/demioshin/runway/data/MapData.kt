package com.demioshin.runway.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.demioshin.runway.data.mapState.INIT

enum class mapState {
    INIT,
    READY,
    RUNNING,
    STOPPED,
}

data class MapData(
    var state: mapState = INIT,
    val uiSettings: MutableState<MapUiSettings> = mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)),
    val properties: MutableState<MapProperties> = mutableStateOf(
        MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = true)),
    var location: LatLng = LatLng(0.0, 0.0)
    )