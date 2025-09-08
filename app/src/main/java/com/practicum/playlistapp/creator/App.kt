package com.practicum.playlistapp

import android.app.Application
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository

class App : Application() {
    private lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        Creator.initialize(this)

        // Получаем репозиторий через Creator, что обеспечивает инверсию зависимостей
        themeRepository = Creator.provideThemeRepository()
        themeRepository.applyTheme(themeRepository.getCurrentTheme())
    }
}