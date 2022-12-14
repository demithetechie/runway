package com.demioshin.runway

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel: ViewModel() {
    var userCurrentLng = mutableStateOf(-1.158109)
    var userCurrentLat = mutableStateOf(52.954784)
}