package com.practicum.playlistmaker.features.search

import android.content.Context
import com.practicum.playlistmaker.features.search.data.RecentTracksDiskDataSource
import com.practicum.playlistmaker.features.search.data.RecentTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.data.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.impl.RecentTracksInteractorImpl
import com.practicum.playlistmaker.features.search.domain.impl.SearchTracksInteractorImpl

object Creator {
    fun provideRecentTracksInteractor(context: Context): RecentTracksInteractor {
        return RecentTracksInteractorImpl(RecentTracksRepositoryImpl(RecentTracksDiskDataSource((context))))
    }

    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(SearchTracksRepositoryImpl())
    }
}
