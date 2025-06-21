package com.practicum.playlistapp

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

const val fileThemePreferences = "theme_preferences"
const val keyTheme = "key_my_theme"

class App : Application() {

    var darkTheme = false
    private lateinit var sharedPref: SharedPreferences



    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(fileThemePreferences, MODE_PRIVATE)
        darkTheme = sharedPref.getBoolean(keyTheme, darkTheme)
        themeNow()

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPref.edit().putBoolean(keyTheme,darkTheme).apply()
        themeNow()
    }
    fun themeNow(){
        AppCompatDelegate.setDefaultNightMode(if (darkTheme) MODE_NIGHT_YES else MODE_NIGHT_NO)
    }
}
