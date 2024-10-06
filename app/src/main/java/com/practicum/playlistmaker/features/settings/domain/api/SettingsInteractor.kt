package com.practicum.playlistmaker.features.settings.domain.api

interface SettingsInteractor {
    fun getIsDarkTheme(): Boolean
    fun setIsDarkTheme(value: Boolean)
    fun setDefaultIsDarkTheme(value: Boolean)
}