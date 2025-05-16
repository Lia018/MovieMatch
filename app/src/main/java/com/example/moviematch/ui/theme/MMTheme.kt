package com.example.moviematch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    background = Color(0xFF5EC5D4),
    primary = Color(0xFFA782EC),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    //onSecondary = Color(0xFF121212),
)

private val DarkColors = darkColorScheme(
    //primary = Color(0xFFA7C7F0),
    primary = Color(0xFF1E1A64),
    background = Color(0xFF6F2DA8),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFFFFFFFF),
)

@Composable
fun MMTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}

