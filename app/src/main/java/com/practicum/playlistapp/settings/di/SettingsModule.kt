package com.practicum.playlistapp.settings.di

import com.practicum.playlistapp.search.data.sharedPrefs.ThemePreferences
import com.practicum.playlistapp.settings.data.repositoryImpl.ThemeRepositoryImpl
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository
import com.practicum.playlistapp.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module{
    single { ThemePreferences(androidContext()) }
    single<ThemeRepository> {
        ThemeRepositoryImpl(
            themePreferences = get(),
            context = androidContext()
        )
    }
    viewModel {
        SettingsViewModel(
            themeRepository = get(),
            sharingInteractor = get()
        )
    }


}