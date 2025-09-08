package com.practicum.playlistapp.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository
import com.practicum.playlistapp.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val themeRepository: ThemeRepository,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    val isDarkTheme: Boolean
        get() = themeRepository.getCurrentTheme()

    fun setTheme(isDark: Boolean) {
        themeRepository.setTheme(isDark)
    }

    fun shareLink() = sharingInteractor.shareLink()

    fun writeSupport() = sharingInteractor.writeSupport()

    fun agreement() = sharingInteractor.agreement()

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val themeRepository = Creator.provideThemeRepository()
                val sharingInteractor = Creator.provideSharingInteractor()
                SettingsViewModel(themeRepository, sharingInteractor)
            }
        }
    }
}