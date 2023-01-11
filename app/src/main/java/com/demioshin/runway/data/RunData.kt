package com.demioshin.runway.data

import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import java.sql.Time
import java.util.Timer
import kotlin.concurrent.thread

class RunData {
    var time = MutableLiveData<Time>(Time(0, 0, 0))
    var steps = MutableLiveData<Int>(0)
    var distance = MutableLiveData<Int>(0)
}