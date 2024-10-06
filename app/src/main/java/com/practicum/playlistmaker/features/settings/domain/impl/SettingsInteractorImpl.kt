package com.practicum.playlistmaker.features.settings.domain.impl

import com.practicum.playlistmaker.features.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.features.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(
    val settingsRepository: SettingsRepository,
): SettingsInteractor {
    override fun getIsDarkTheme(): Boolean {
       return settingsRepository.getIsDarkTheme()
    }

    override fun setIsDarkTheme(value: Boolean) {
        settingsRepository.setIsDarkTheme(value)
    }

    override fun setDefaultIsDarkTheme(value: Boolean) {
        settingsRepository.setDefaultIsDarkTheme(value)
    }
}
