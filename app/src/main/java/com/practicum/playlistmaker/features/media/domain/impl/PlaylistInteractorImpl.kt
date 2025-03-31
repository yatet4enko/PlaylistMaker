package com.practicum.playlistmaker.features.media.domain.impl

import com.practicum.playlistmaker.features.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.features.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
): PlaylistInteractor {
    override suspend fun add(playlist: Playlist) {
        playlistRepository.add(playlist)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, trackId: Int) {
        playlistRepository.addTrackToPlaylist(playlist, trackId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun removePlaylist(playlistId: Int) {
        playlistRepository.removePlaylist(playlistId)
    }

    override fun getAll(): Flow<List<Playlist>> {
        return playlistRepository.getAll()
    }

    override fun getPlaylist(id: Int): Flow<Playlist?> {
        return playlistRepository.getPlaylist(id)
    }

    override suspend fun getPlaylistTracks(trackIds: List<Int>): List<Track> {
        return playlistRepository.getPlaylistTracks(trackIds)
    }
}