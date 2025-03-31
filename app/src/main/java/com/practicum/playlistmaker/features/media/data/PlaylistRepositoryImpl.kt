package com.practicum.playlistmaker.features.media.data

import com.practicum.playlistmaker.features.media.data.db.AppDatabase
import com.practicum.playlistmaker.features.media.data.formatters.PlaylistFormatter
import com.practicum.playlistmaker.features.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import com.practicum.playlistmaker.features.search.data.formatters.TrackFormatter
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val db: AppDatabase,
    private val playlistFormatter: PlaylistFormatter,
    private val trackFormatter: TrackFormatter,
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

    override suspend fun updatePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            db.playlistDao().updatePlaylist(
                playlistFormatter.toEntity(playlist)
            )
        }
    }

    private suspend fun removeTrackIfNeNuzhen(trackId: Int) {
        val allPlaylists = db.playlistDao().getAll().first().map { playlistFormatter.fromEntity(it) }
        val playlistWithTrack = allPlaylists.firstOrNull {
            it.trackIds.contains(trackId)
        }

        if (playlistWithTrack == null) {
            db.trackDao().removeTrackById(trackId)
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        val playlist = getPlaylist(playlistId).first()

        playlist?.let { playlist ->
            val id = playlist.id ?: return

            db.playlistDao()
                .updatePlaylistTracks(
                    id,
                    (playlist.trackIds - trackId).joinToString(separator = ",")
                )


            removeTrackIfNeNuzhen(trackId)
        }
    }

    override suspend fun removePlaylist(playlistId: Int) {
        withContext(Dispatchers.IO) {
            val playlist = db.playlistDao().getPlaylistById(playlistId).first()?.let { playlistFormatter.fromEntity(it) } ?: return@withContext
            val trackIds = playlist.trackIds

            db.playlistDao().removePlaylistById(playlistId)

            trackIds.forEach { trackId ->
                removeTrackIfNeNuzhen(trackId)
            }
        }
    }

    override fun getAll(): Flow<List<Playlist>> {
        return db.playlistDao()
            .getAll().map {
                it.map {
                    playlistFormatter.fromEntity(it)
                }
            }
    }

    override fun getPlaylist(id: Int): Flow<Playlist?> {
        return db.playlistDao()
            .getPlaylistById(id)
            .map { it?.let {
                playlistFormatter.fromEntity(it)
            } }
    }

    override suspend fun getPlaylistTracks(trackIds: List<Int>): List<Track> {
        return withContext(Dispatchers.IO) {
            db.trackDao().getAllTracks().filter { track ->
                trackIds.contains(track.id)
            }.map {
                trackFormatter.fromEntity(it)
            }
        }
    }
}
