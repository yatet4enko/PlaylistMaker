package com.practicum.playlistmaker.features.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey
    val id: Int,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val year: Int?,
    val primaryGenreName: String,
    val country: String,
    val trackTime: String,
    val previewUrl: String?,
)