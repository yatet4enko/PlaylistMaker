package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.media.data.db.AppDatabase
import com.practicum.playlistmaker.features.search.data.formatters.TrackFormatter
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecentTracksRepositoryImpl(
    private val recentTracksDiskDataSource: RecentTracksDiskDataSource,
    private val trackFormatter: TrackFormatter,
    private val db: AppDatabase,
): RecentTracksRepository {
    override fun clear() {
        recentTracksDiskDataSource.clear()
    }

    override fun add(track: Track): List<Track> {
        return recentTracksDiskDataSource
            .add(trackFormatter.toDto(track))
            .map { trackFormatter.fromDto(it) }
    }

    override suspend fun getRecentTracks(): Flow<List<Track>> = flow {
        val favoriteTracksIds = db.trackDao().getAllTrackIds()

        emit(
            recentTracksDiskDataSource.getRecentTracks()
                .map { trackFormatter.fromDto(it).copy(isFavorite = favoriteTracksIds.contains(it.id)) }
        )
    }
}
