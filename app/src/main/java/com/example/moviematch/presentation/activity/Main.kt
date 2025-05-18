package com.example.moviematch.presentation.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.example.moviematch.presentation.navigation.AppNavigation
import com.example.moviematch.ui.theme.MMTheme
import java.util.Locale

/**
 * The main entry point of the application.
 * Responsible for applying theme and language settings and initializing the navigation graph.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is starting. Sets up the Compose content and app-wide configurations.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Access shared preferences to load stored theme and language settings
            val appContext = applicationContext
            val prefs = appContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val savedLang = prefs.getString("lang", "en") ?: "en"
            val savedTheme = prefs.getString("selected_theme", "light") ?: "light"

            // States to hold the current language and theme
            var language by remember { mutableStateOf(savedLang) }
            var theme by remember { mutableStateOf(savedTheme) }

            // Create a localized context based on the selected language
            val localizedContext = remember(language) {
                val config = Configuration(resources.configuration)
                config.setLocale(Locale(language))
                createConfigurationContext(config)
            }

            // Provide the localized context to the composable
            CompositionLocalProvider(LocalContext provides localizedContext) {
                // Apply the selected theme (dark/light)
                MMTheme(useDarkTheme = theme == "dark") {

                    // Set up the main navigation graph and pass handlers for theme/language changes
                    AppNavigation(
                        isDarkTheme = theme == "dark",
                        onLanguageChange = {
                            language = it
                            prefs.edit { putString("lang", it) }
                        },
                        onThemeToggle = {
                            theme = if (theme == "light") "dark" else "light"
                            prefs.edit { putString("selected_theme", theme) }
                        }
                    )
                }
            }
        }
    }
}
