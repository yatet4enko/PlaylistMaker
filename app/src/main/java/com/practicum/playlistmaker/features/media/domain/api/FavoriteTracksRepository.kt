package com.practicum.playlistmaker.features.media.domain.api

import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun add(track: Track)
    suspend fun remove(track: Track)
    suspend fun getAll(): Flow<List<Track>>
    suspend fun getAllIds(): Flow<List<Int>>
}
