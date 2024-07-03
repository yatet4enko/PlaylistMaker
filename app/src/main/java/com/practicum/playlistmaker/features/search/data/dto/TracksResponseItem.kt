package com.practicum.playlistmaker.features.search.data.dto

data class TracksResponseItem(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
)
