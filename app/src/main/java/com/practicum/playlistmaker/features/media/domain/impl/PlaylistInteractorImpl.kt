package com.practicum.playlistmaker.features.media.domain.impl

import com.practicum.playlistmaker.features.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.features.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.features.media.domain.models.Playlist
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

    override fun getAll(): Flow<List<Playlist>> {
        return playlistRepository.getAll()
    }
}