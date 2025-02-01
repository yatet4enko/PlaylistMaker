package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.features.media.domain.impl.FavoriteTracksInteractorImpl
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.features.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.impl.RecentTracksInteractorImpl
import com.practicum.playlistmaker.features.search.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.features.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.features.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val interactorsModule = module{
    single<RecentTracksInteractor> {
        RecentTracksInteractorImpl(get())
    }

    single<SearchTracksInteractor> {
        SearchTracksInteractorImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }
}
