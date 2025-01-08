package com.practicum.playlistmaker.features.search.domain.models

data class Track(
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
    val isFavorite: Boolean = false,
)
