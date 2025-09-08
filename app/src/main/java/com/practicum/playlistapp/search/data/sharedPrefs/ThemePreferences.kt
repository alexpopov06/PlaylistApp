package com.practicum.playlistapp.search.data.sharedPrefs

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(private val context: Context) {
    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    }

    fun isDarkTheme(): Boolean {
        return sharedPref.getBoolean("dark_theme", false)
    }

    fun setDarkTheme(enabled: Boolean) {
        sharedPref.edit().putBoolean("dark_theme", enabled).apply()
    }
}