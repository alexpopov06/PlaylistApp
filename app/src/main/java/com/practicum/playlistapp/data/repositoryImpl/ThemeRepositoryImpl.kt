package com.practicum.playlistapp.data.repositoryImpl

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistapp.data.sharedPrefs.ThemePreferences
import com.practicum.playlistapp.domain.repository.ThemeRepository

class ThemeRepositoryImpl(private val themePreferences: ThemePreferences,
                          private val context: Context): ThemeRepository {
    override fun getCurrentTheme(): Boolean {
        return themePreferences.isDarkTheme()
    }

    override fun setTheme(isDark: Boolean) {
        themePreferences.setDarkTheme(isDark)
        applyTheme(isDark)
    }
    override fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}