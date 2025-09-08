package com.practicum.playlistapp.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository

class SettingsViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    val isDarkTheme: Boolean
        get() = themeRepository.getCurrentTheme()

    fun setTheme(isDark: Boolean) {
        themeRepository.setTheme(isDark)
    }

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val themeRepository = Creator.provideThemeRepository()
                SettingsViewModel(themeRepository)
            }
        }
    }
}