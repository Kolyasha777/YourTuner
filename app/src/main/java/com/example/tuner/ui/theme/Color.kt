package com.example.tuner.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import com.rohankhayech.android.util.ui.theme.trueDarkColors

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF388E3C)
val Blue500 = Color(0xFF2196F3)
val Blue700 = Color(0xFF1976D2)
val Red500 = Color(0xFFF44336)
val Red700 = Color(0xFFD32F2F)
val Yellow500 = Color(0xFFD9BF00)

val LightColors = lightColors(
    primary = Green500,
    primaryVariant = Green700,
    secondary = Blue500,
    secondaryVariant = Blue700,
    onSecondary = Color.White,
    error = Red700,
)

val DarkColors = darkColors(
    primary = Green500,
    primaryVariant = Green700,
    secondary = Blue500,
    secondaryVariant = Blue700,
    onSecondary = Color.White,
    error = Red500,
)

val BlackColors = trueDarkColors(
    primary = Green700,
    primaryVariant = Green700,
    secondary = Blue700,
    secondaryVariant = Blue700,
    onSecondary = Color.White,
    error = Red500,
)