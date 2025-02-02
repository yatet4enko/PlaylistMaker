package com.practicum.playlistmaker.features.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val artworkFilename: String,
    val tracksIds: String,
    val tracksCount: Int,
)