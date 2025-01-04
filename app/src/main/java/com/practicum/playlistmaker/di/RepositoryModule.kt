package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.features.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.features.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.features.search.data.RecentTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.data.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksRepository
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.features.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.features.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    // Search

    single<RecentTracksRepository> {
        RecentTracksRepositoryImpl(get(), get())
    }

    single<SearchTracksRepository> {
        SearchTracksRepositoryImpl(get())
    }

    // Player

    // Вопрос
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    // Settings

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

}