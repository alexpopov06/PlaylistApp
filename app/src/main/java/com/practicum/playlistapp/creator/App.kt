package com.practicum.playlistapp

import android.app.Application
import com.practicum.playlistapp.media.mediaModule
import com.practicum.playlistapp.playlist.di.playlistModule
import com.practicum.playlistapp.player.di.playerModule
import com.practicum.playlistapp.search.di.searchModule
import com.practicum.playlistapp.settings.di.settingsModule
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository
import com.practicum.playlistapp.sharing.di.sharingModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(settingsModule, sharingModule, playerModule, searchModule, mediaModule, playlistModule)
        }
        val themeRepository = getKoin().get<ThemeRepository>()
        themeRepository.applyTheme(themeRepository.getCurrentTheme())

    }
}