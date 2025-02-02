package com.practicum.playlistmaker.features.media.data.formatters

import com.practicum.playlistmaker.features.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.features.media.data.db.TrackEntity
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import com.practicum.playlistmaker.features.search.data.dto.TrackDto
import com.practicum.playlistmaker.features.search.domain.models.Track

class PlaylistFormatter {
    fun fromEntity(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            artworkFilename = entity.artworkFilename,
            description = entity.description,
            trackIds = if (entity.tracksIds.isEmpty()) {
                emptyList()
            } else {
                entity.tracksIds.split(",").map { it.toInt()  }
            }
        )
    }

    fun toEntity(playlist: Playlist): PlaylistEntity {
        return if (playlist.id == null) {
            PlaylistEntity(
                name = playlist.name,
                description = playlist.description,
                artworkFilename = playlist.artworkFilename,
                tracksIds = playlist.trackIds.joinToString(separator = ","),
                tracksCount = playlist.trackIds.size,
            )
        } else {
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                artworkFilename = playlist.artworkFilename,
                tracksIds = playlist.trackIds.joinToString(separator = ","),
                tracksCount = playlist.trackIds.size,
            )
        }
    }
}
