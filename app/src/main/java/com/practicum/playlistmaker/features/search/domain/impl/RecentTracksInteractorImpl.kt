package com.practicum.playlistmaker.features.search.domain.impl

import com.practicum.playlistmaker.features.search.domain.api.RecentTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class RecentTracksInteractorImpl(
    private val recentTracksRepository: RecentTracksRepository,
): RecentTracksInteractor {
    override fun clear() {
        recentTracksRepository.clear()
    }

    override fun add(track: Track): List<Track> {
        return recentTracksRepository.add(track)
    }

    override suspend fun getRecentTracks(): Flow<List<Track>> {
        return recentTracksRepository.getRecentTracks()
    }

}