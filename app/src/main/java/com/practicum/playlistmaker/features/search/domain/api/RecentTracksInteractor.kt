package com.practicum.playlistmaker.features.search.domain.api

import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface RecentTracksInteractor {
    fun clear()
    fun add(track: Track): List<Track>
    suspend fun getRecentTracks(): Flow<List<Track>>
}
