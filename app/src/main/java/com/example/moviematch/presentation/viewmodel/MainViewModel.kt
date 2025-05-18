/*package com.example.moviematch.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import androidx.core.content.edit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(prefs.getString("selected_theme", "light") ?: "light")
    val theme: StateFlow<String> = _theme

    //private val _language = MutableStateFlow(prefs.getString("lang", "en") ?: "en")
    private val _language = MutableStateFlow(prefs.getString("lang", "en") ?: "en")
    val language: StateFlow<String> = _language


    fun toggleTheme() {
        val newTheme = if (_theme.value == "light") "dark" else "light"
        _theme.value = newTheme
        prefs.edit { putString("selected_theme", newTheme) }
    }

    fun setLanguage(code: String) {
        _language.value = code
        prefs.edit { putString("lang", code) }
        applyLocale(code)
    }

    private fun applyLocale(languageCode: String) {
        val context = getApplication<Application>()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}*/
