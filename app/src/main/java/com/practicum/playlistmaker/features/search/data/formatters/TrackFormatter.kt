package com.practicum.playlistmaker.features.search.data.formatters

import com.practicum.playlistmaker.features.media.data.db.TrackEntity
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
            previewUrl = dto.previewUrl,
            trackTimeMillis = dto.trackTimeMillis,
            isFavorite = dto.isFavorite,
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
            previewUrl = track.previewUrl,
            trackTimeMillis = track.trackTimeMillis,
            isFavorite = track.isFavorite,
        )
    }

    fun fromEntity(entity: TrackEntity): Track {
        return Track(
            id = entity.id,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = formatMillisToMinutesAndSeconds(entity.trackTimeMillis),
            artworkUrl100 = entity.artworkUrl100,
            year = entity.year,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            collectionName = entity.collectionName,
            previewUrl = entity.previewUrl,
            trackTimeMillis = entity.trackTimeMillis,
            isFavorite = entity.isFavorite,
        )
    }

    fun toEntity(track: Track): TrackEntity {
        return TrackEntity(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            artworkUrl100 = track.artworkUrl100,
            year = track.year,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            collectionName = track.collectionName?: "",
            previewUrl = track.previewUrl,
            trackTimeMillis = track.trackTimeMillis,
            isFavorite = track.isFavorite,
        )
    }

    fun formatMillisToMinutesAndSeconds(millis: Int): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d", minutes, seconds)
    }
}
