package com.practicum.playlistmaker.features.settings.domain.api

interface SettingsRepository {
    fun getIsDarkTheme(): Boolean
    fun setIsDarkTheme(value: Boolean)
    fun setDefaultIsDarkTheme(value: Boolean)
}
