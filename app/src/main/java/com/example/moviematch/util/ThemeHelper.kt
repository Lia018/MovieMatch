package com.example.moviematch.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object ThemeHelper {
    private const val PREF_NAME = "app_settings"
    private const val THEME_KEY = "selected_theme"

    fun applySavedTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        when (prefs.getString(THEME_KEY, "light")) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(THEME_KEY, "light") == "dark"
    }

    fun setDarkMode(context: Context, useDarkMode: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(THEME_KEY, if (useDarkMode) "dark" else "light") }

        AppCompatDelegate.setDefaultNightMode(
            if (useDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }


}
