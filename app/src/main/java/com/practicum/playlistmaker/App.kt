package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.features.settings.domain.api.SettingsInteractor

class App: Application() {

    var creator = Creator(this)

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        settingsInteractor = (applicationContext as App).creator.provideSettingsInteractor()

        settingsInteractor.setDefaultIsDarkTheme(getSystemIsDarkTheme())

        switchTheme(settingsInteractor.getIsDarkTheme())
    }

    private fun getSystemIsDarkTheme(): Boolean {
        val nightModeFlags: Int = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsInteractor.setIsDarkTheme(darkThemeEnabled)

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}