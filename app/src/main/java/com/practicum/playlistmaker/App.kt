package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {

    var darkTheme = false

    var preferences: SharedPreferences? = null
    override fun onCreate() {
        super.onCreate()

        darkTheme = getSharedPreferences().getBoolean(SHARED_PREFERENCES_DARK_THEME_KEY, isDarkTheme())

        switchTheme(darkTheme)
    }

    private fun isDarkTheme(): Boolean {
        val nightModeFlags: Int = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        getSharedPreferences()
            .edit()
            .putBoolean(SHARED_PREFERENCES_DARK_THEME_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun getSharedPreferences(): SharedPreferences {
        if (preferences != null) {
            return preferences!!
        }

        return getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "PLAYLIST_MAKER_SHARED_PREFERENCES"
        private const val SHARED_PREFERENCES_DARK_THEME_KEY = "SHARED_PREFERENCES_DARK_THEME_KEY"
    }
}