package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.data.dto.TrackDto
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track

class RecentTracksRepositoryImpl(
    private val recentTracksDiskDataSource: RecentTracksDiskDataSource,
): RecentTracksRepository {
    override fun clear() {
        recentTracksDiskDataSource.clear()
    }

    override fun add(track: Track): List<Track> {
        return recentTracksDiskDataSource.add(TrackDto(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            year = track.year,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            collectionName = track.collectionName,
            previewUrl = track.previewUrl
        )).map {
            Track(
                id = it.id,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTime = it.trackTime,
                artworkUrl100 = it.artworkUrl100,
                year = it.year,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                collectionName = it.collectionName,
                previewUrl = it.previewUrl
            )
        }
    }

    override fun getRecentTracks(): List<Track> {
        return  recentTracksDiskDataSource.getRecentTracks().map {
            Track(
                id = it.id,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTime = it.trackTime,
                artworkUrl100 = it.artworkUrl100,
                year = it.year,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                collectionName = it.collectionName,
                previewUrl = it.previewUrl
            )
        }
    }
}
