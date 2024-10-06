package com.practicum.playlistmaker.features.settings.data

import com.practicum.playlistmaker.features.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val settingsDiskDataSource: SettingsDiskDataSource,
): SettingsRepository {
    override fun getIsDarkTheme(): Boolean {
        return settingsDiskDataSource.isDarkTheme
    }

    override fun setIsDarkTheme(value: Boolean) {
        settingsDiskDataSource.isDarkTheme = value
    }

    override fun setDefaultIsDarkTheme(value: Boolean) {
        settingsDiskDataSource.setDefaultIsDarkTheme(value)
    }
}
