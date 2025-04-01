package com.practicum.playlistmaker.features.media.domain.api

import com.practicum.playlistmaker.features.media.domain.models.Playlist
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun add(playlist: Playlist)

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int)

    suspend fun removePlaylist(playlistId: Int)

    fun getPlaylist(id: Int): Flow<Playlist?>

    fun getAll(): Flow<List<Playlist>>

    suspend fun getPlaylistTracks(trackIds: List<Int>): List<Track>
}
