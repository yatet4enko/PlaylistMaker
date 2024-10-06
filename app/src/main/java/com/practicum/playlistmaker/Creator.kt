package com.practicum.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.features.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.features.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.features.search.data.RecentTracksDiskDataSource
import com.practicum.playlistmaker.features.search.data.RecentTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.data.SearchTracksRemoteDataSource
import com.practicum.playlistmaker.features.search.data.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.data.formatters.TrackFormatter
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.impl.RecentTracksInteractorImpl
import com.practicum.playlistmaker.features.search.domain.impl.SearchTracksInteractorImpl
import com.practicum.playlistmaker.features.settings.data.SettingsDiskDataSource
import com.practicum.playlistmaker.features.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.features.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.features.settings.domain.impl.SettingsInteractorImpl

class Creator(
    private val context: Context,
) {
    fun provideRecentTracksInteractor(): RecentTracksInteractor {
        return RecentTracksInteractorImpl(
            RecentTracksRepositoryImpl(
                RecentTracksDiskDataSource(context),
                TrackFormatter(),
            )
        )
    }

    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(SearchTracksRepositoryImpl(SearchTracksRemoteDataSource()))
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(SettingsRepositoryImpl(SettingsDiskDataSource(context)))
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return  PlayerInteractorImpl(PlayerRepositoryImpl())
    }
}
