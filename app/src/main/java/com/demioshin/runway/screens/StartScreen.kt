package com.demioshin.runway.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demioshin.runway.ui.theme.backgroundColor
import com.demioshin.runway.ui.theme.buttonColor
import com.demioshin.runway.ui.theme.mainFontColor


@Composable
fun StartScreen() {
    val context = LocalContext.current

    Surface(
        color = backgroundColor
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(Modifier.height(100.dp))
            Text("Runway", color = mainFontColor, style = MaterialTheme.typography.h2)
            Spacer(Modifier.height(200.dp))
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                onClick = {

            }) {
                Text("Get Started", style = MaterialTheme.typography.body1)
            }
        }
    }

}



@Preview(name = "Preview1", device = Devices.PIXEL, showSystemUi = true)
@Composable
fun DefaultPreview() {
    StartScreen()
}