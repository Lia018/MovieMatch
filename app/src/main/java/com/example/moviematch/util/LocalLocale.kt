/*package com.example.moviematch.util

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import java.util.Locale

fun setLocale(context: Context, langCode: String) {
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    val currentLang = prefs.getString("lang", "en") ?: "en"

    if (currentLang == langCode) return

    val locale = Locale(langCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    prefs.edit { putString("lang", langCode) }

    /*val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)*/
}*/

