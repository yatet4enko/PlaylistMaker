package com.practicum.playlistmaker.features.media.domain.models

data class Playlist(
    val id: Int? = null,
    val name: String,
    val artworkFilename: String,
    val description: String,
    val trackIds: List<Int>,
)
