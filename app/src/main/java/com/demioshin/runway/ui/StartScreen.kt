package com.demioshin.runway.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demioshin.runway.ui.theme.backgroundColor
import com.demioshin.runway.ui.theme.buttonColor
import com.demioshin.runway.ui.theme.mainFontColor



@Composable
fun StartScreen(navController: NavController) {
    Surface(
        color = backgroundColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(Modifier.height(100.dp))
            Text("Runway", color = buttonColor, style = MaterialTheme.typography.h2, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(100.dp))
            Text("Your friendly running tracker, \n" +
                    "designed to help you \n" +
                    "run the show!", color = mainFontColor, style = MaterialTheme.typography.body1, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
            Spacer(Modifier.height(250.dp))
            Button(
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(40),
                colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                onClick = {
                    navController.navigate("map")
            }) {
                Text("Get Started", color = mainFontColor, style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }

}



//@Preview(name = "Preview1", device = Devices.PIXEL, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    StartScreen()
//}