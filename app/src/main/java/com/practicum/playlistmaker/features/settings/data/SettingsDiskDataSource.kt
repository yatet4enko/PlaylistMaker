package com.practicum.playlistmaker.features.settings.data

import android.app.Application.MODE_PRIVATE
import android.content.Context
import android.content.SharedPreferences

class SettingsDiskDataSource(
    private val context: Context,
) {
    private var preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

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
        private const val SHARED_PREFERENCES_NAME = "PLAYLIST_MAKER_SHARED_PREFERENCES"
        private const val SHARED_PREFERENCES_DARK_THEME_KEY = "SHARED_PREFERENCES_DARK_THEME_KEY"
    }
}