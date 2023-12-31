package com.demioshin.runway.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.demioshin.runway.R

val fontytont = FontFamily (
    Font(R.font.glaciareg, style = FontStyle.Normal),
    Font(R.font.glacialitalic, style = FontStyle.Italic),
    Font(R.font.glacialbold, weight = FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = fontytont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = fontytont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    h2 = TextStyle(
        fontFamily = fontytont,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    ),
    h3 = TextStyle(
        fontFamily = fontytont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp
    ),
    h4 = TextStyle(
        fontFamily = fontytont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp
    )
)

