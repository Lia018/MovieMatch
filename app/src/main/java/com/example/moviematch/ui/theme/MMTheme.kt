package com.example.moviematch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Light color scheme used when the application is in light theme mode.
 */
private val LightColors = lightColorScheme(
    background = Color(0xFF5EC5D4),
    primary = Color(0xFFA782EC),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000)
    //onSecondary = Color(0xFF121212)
)

/**
 * Dark color scheme used when the application is in dark theme mode.
 */
private val DarkColors = darkColorScheme(
    //primary = Color(0xFFA7C7F0),
    primary = Color(0xFF1E1A64),
    background = Color(0xFF6F2DA8),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFFFFFFFF)
)

/**
 * Composable function that provides the custom theme for the MovieMatch app.
 *
 * Applies a [MaterialTheme] to the composable hierarchy based on whether the system is
 * currently using dark mode or not, unless overridden by the [useDarkTheme] parameter.
 *
 * The theme includes a color scheme and default typography.
 *
 * @param useDarkTheme Determines whether to apply the dark theme. Defaults to system setting.
 * @param content The composable content to which the theme should be applied.
 */
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
