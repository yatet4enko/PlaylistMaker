package com.practicum.playlistmaker.features.settings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.features.settings.ui.models.SettingsStateVO

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val settingsInteractor = getApplication<App>().creator.provideSettingsInteractor()

    private val settingsStateLiveData = MutableLiveData(SettingsStateVO(
        isDarkTheme = settingsInteractor.getIsDarkTheme()
    ))
    val settingsState: LiveData<SettingsStateVO> = settingsStateLiveData

    fun onDarkThemeSwitch(isDarkTheme: Boolean) {
        getApplication<App>().switchTheme(isDarkTheme)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}
