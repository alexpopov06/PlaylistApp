package com.practicum.playlistapp.ui.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.practicum.playlistapp.data.repositoryImpl.ThemeRepositoryImpl
import com.practicum.playlistapp.data.sharedPrefs.ThemePreferences
import com.practicum.playlistapp.domain.repository.ThemeRepository



class App : Application() {
    lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()


        themeRepository = ThemeRepositoryImpl(
            ThemePreferences(this),
            this
        )
        themeRepository.applyTheme(themeRepository.getCurrentTheme())
    }
}
