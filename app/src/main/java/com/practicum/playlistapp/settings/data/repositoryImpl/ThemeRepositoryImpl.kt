package com.practicum.playlistapp.settings.data.repositoryImpl

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository
import com.practicum.playlistapp.search.data.sharedPrefs.ThemePreferences

class ThemeRepositoryImpl(private val themePreferences: ThemePreferences,
                          private val context: Context
): ThemeRepository {
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