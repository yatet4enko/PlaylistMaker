package com.practicum.playlistmaker.features.search.data.dto

data class TracksResponseItem(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val releaseDate: String? = null,
    val primaryGenreName: String,
    val country: String,
    val collectionName: String? = null,
    val previewUrl: String? = null,
)
