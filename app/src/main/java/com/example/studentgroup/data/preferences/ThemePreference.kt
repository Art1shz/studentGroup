package com.example.studentgroup.data.preferences

import android.content.Context
import android.content.SharedPreferences

class ThemePreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_DARK_THEME = "is_dark_theme"
    }
} 