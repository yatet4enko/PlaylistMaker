package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.data.formatters.TrackFormatter
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track

class RecentTracksRepositoryImpl(
    private val recentTracksDiskDataSource: RecentTracksDiskDataSource,
    private val trackFormatter: TrackFormatter,
): RecentTracksRepository {
    override fun clear() {
        recentTracksDiskDataSource.clear()
    }

    override fun add(track: Track): List<Track> {
        return recentTracksDiskDataSource
            .add(trackFormatter.toDto(track))
            .map { trackFormatter.fromDto(it) }
    }

    override fun getRecentTracks(): List<Track> {
        return  recentTracksDiskDataSource.getRecentTracks().map { trackFormatter.fromDto(it) }
    }
}
