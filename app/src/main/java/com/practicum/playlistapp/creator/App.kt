package com.practicum.playlistapp.creator

import android.app.Application
import com.practicum.playlistapp.search.data.sharedPrefs.ThemePreferences
import com.practicum.playlistapp.settings.data.repositoryImpl.ThemeRepositoryImpl
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository

class App : Application() {
    lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        Creator.initialize(this)


        themeRepository = ThemeRepositoryImpl(
            ThemePreferences(this),
            this
        )
        themeRepository.applyTheme(themeRepository.getCurrentTheme())
    }
}