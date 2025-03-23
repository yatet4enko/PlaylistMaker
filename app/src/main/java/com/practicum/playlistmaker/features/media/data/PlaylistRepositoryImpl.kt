package com.practicum.playlistmaker.features.media.data

import com.practicum.playlistmaker.features.media.data.db.AppDatabase
import com.practicum.playlistmaker.features.media.data.formatters.PlaylistFormatter
import com.practicum.playlistmaker.features.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val db: AppDatabase,
    private val playlistFormatter: PlaylistFormatter,
): PlaylistRepository {
    override suspend fun add(playlist: Playlist) {
        db.playlistDao()
            .insertPlaylist(playlistFormatter.toEntity(playlist))
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, trackId: Int) {
        val id = playlist.id ?: return

        db.playlistDao()
            .updatePlaylistTracks(
                id,
                (playlist.trackIds + trackId).joinToString(separator = ",")
            )
    }

    override fun getAll(): Flow<List<Playlist>> {
        return db.playlistDao()
            .getAll().map {
                it.map {
                    playlistFormatter.fromEntity(it)
                }
            }
    }
}
