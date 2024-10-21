package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.features.player.ui.PlayerViewModel
import com.practicum.playlistmaker.features.search.ui.SearchViewModel
import com.practicum.playlistmaker.features.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(androidApplication(), get())
    }

    viewModel {
        SearchViewModel(androidApplication(), get(), get())
    }

    viewModel {
        SettingsViewModel(androidApplication(), get())
    }
}


