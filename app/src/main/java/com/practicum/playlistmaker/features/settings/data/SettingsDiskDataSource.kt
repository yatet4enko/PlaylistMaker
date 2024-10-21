package com.practicum.playlistmaker.features.settings.data

import android.content.SharedPreferences

class SettingsDiskDataSource(
    private val preferences: SharedPreferences,
) {
    private var defaultIsDarkTheme = false

    fun setDefaultIsDarkTheme(value: Boolean) {
        defaultIsDarkTheme = value
    }

    var isDarkTheme: Boolean
        get() = preferences.getBoolean(SHARED_PREFERENCES_DARK_THEME_KEY, defaultIsDarkTheme)
        set(value) {
            preferences.edit()
                .putBoolean(SHARED_PREFERENCES_DARK_THEME_KEY, value)
                .apply()
        }

    companion object {
        private const val SHARED_PREFERENCES_DARK_THEME_KEY = "SHARED_PREFERENCES_DARK_THEME_KEY"
    }
}