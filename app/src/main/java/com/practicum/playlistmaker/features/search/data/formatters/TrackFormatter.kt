package com.practicum.playlistmaker.features.search.data.formatters

import com.practicum.playlistmaker.features.search.data.dto.TrackDto
import com.practicum.playlistmaker.features.search.domain.models.Track

class TrackFormatter {
    fun fromDto(dto: TrackDto): Track {
        return Track(
            id = dto.id,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTime = dto.trackTime,
            artworkUrl100 = dto.artworkUrl100,
            year = dto.year,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            collectionName = dto.collectionName,
            previewUrl = dto.previewUrl
        )
    }

    fun toDto(track: Track): TrackDto {
        return TrackDto(
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
        )
    }
}
