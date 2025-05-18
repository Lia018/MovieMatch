package com.example.moviematch.presentation.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.moviematch.presentation.navigation.AppNavigation
import com.example.moviematch.ui.theme.MMTheme
import java.util.Locale
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val appContext = applicationContext
            val prefs = appContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            val savedLang = prefs.getString("lang", "en") ?: "en"
            val savedTheme = prefs.getString("selected_theme", "light") ?: "light"

            var language by remember { mutableStateOf(savedLang) }
            var theme by remember { mutableStateOf(savedTheme) }

            val localizedContext = remember(language) {
                val config = Configuration(resources.configuration)
                config.setLocale(Locale(language))
                createConfigurationContext(config)
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                MMTheme(useDarkTheme = theme == "dark") {

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
