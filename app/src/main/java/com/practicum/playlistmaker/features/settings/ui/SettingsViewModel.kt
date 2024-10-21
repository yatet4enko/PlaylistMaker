package com.practicum.playlistmaker.features.settings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.features.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.features.settings.ui.models.SettingsStateVO

class SettingsViewModel(
    application: Application,
    private val settingsInteractor: SettingsInteractor,
): AndroidViewModel(application) {

    private val settingsStateLiveData = MutableLiveData(SettingsStateVO(
        isDarkTheme = settingsInteractor.getIsDarkTheme()
    ))
    val settingsState: LiveData<SettingsStateVO> = settingsStateLiveData

    fun onDarkThemeSwitch(isDarkTheme: Boolean) {
        getApplication<App>().switchTheme(isDarkTheme)
    }
}
