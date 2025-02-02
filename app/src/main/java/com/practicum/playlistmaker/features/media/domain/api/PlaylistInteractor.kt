package com.practicum.playlistmaker.features.media.domain.api

import com.practicum.playlistmaker.features.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun add(playlist: Playlist)

    suspend fun addTrackToPlaylist(playlist: Playlist, trackId: Int)

    fun getAll(): Flow<List<Playlist>>
}
