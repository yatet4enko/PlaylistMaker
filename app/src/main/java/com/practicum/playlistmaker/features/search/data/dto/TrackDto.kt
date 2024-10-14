package com.practicum.playlistmaker.features.search.data.dto

data class TrackDto(
    val id: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val year: Int,
    val primaryGenreName: String,
    val country: String,
    val collectionName: String?,
    val previewUrl: String,
)
