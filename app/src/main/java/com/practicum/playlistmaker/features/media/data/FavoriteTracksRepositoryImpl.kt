package com.practicum.playlistmaker.features.media.data

import com.practicum.playlistmaker.features.media.data.db.AppDatabase
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksRepository
import com.practicum.playlistmaker.features.search.data.formatters.TrackFormatter
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val db: AppDatabase,
    private val trackFormatter: TrackFormatter,
): FavoriteTracksRepository {
    override suspend fun add(track: Track) {
        db.trackDao()
            .removeTrack(trackFormatter.toEntity(track))

        db.trackDao()
            .insertTrack(trackFormatter.toEntity(track.copy(isFavorite = true)))
    }

    override suspend fun remove(track: Track) {
        db.trackDao()
            .removeTrack(trackFormatter.toEntity(track))

        db.trackDao()
            .insertTrack(trackFormatter.toEntity(track.copy(isFavorite = false)))
    }

    override fun getAll(): Flow<List<Track>> {
        return db.trackDao().getAllTracks().map {
            it.map { trackFormatter.fromEntity(it) }
        }
    }


    override suspend fun getAllIds(): Flow<List<Int>> {
        return db.trackDao().getAllTracks().map {
            it.filter { it.isFavorite }.map { it.id }
        }
    }
}
