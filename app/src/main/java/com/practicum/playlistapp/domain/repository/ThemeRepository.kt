package com.practicum.playlistapp.domain.repository

interface ThemeRepository {
    fun getCurrentTheme(): Boolean
    fun setTheme(isDark: Boolean)
    fun applyTheme(isDark: Boolean)
}