package com.practicum.playlistmaker.features.search.data.dto

data class Track(
    val id: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
)
