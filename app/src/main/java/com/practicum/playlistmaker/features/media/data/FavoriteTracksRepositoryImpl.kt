package com.practicum.playlistmaker.features.media.data

import com.practicum.playlistmaker.features.media.data.db.AppDatabase
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksRepository
import com.practicum.playlistmaker.features.search.data.formatters.TrackFormatter
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val db: AppDatabase,
    private val trackFormatter: TrackFormatter,
): FavoriteTracksRepository {
    override suspend fun add(track: Track) {
        db.trackDao()
            .insertTrack(trackFormatter.toEntity(track))
    }

    override suspend fun remove(track: Track) {
        db.trackDao()
            .removeTrack(trackFormatter.toEntity(track))

    }

    override suspend fun getAll(): Flow<List<Track>> = flow {
        emit(db.trackDao().getAllTracks().map { trackFormatter.fromEntity(it) })
    }

    override suspend fun getAllIds(): Flow<List<Int>> = flow {
        emit(db.trackDao().getAllTracks().map { it.id })
    }
}
