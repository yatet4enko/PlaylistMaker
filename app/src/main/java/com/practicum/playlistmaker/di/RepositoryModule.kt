package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.features.media.data.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.features.media.data.ImageRepositoryImpl
import com.practicum.playlistmaker.features.media.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksRepository
import com.practicum.playlistmaker.features.media.domain.api.ImageRepository
import com.practicum.playlistmaker.features.media.domain.api.PlaylistRepository
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
        RecentTracksRepositoryImpl(get(), get(), get())
    }

    single<SearchTracksRepository> {
        SearchTracksRepositoryImpl(get(), get())
    }

    // Player

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    // Settings

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    // Media
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<ImageRepository> {
        ImageRepositoryImpl(get())
    }

}