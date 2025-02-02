package com.practicum.playlistmaker.features.media.ui.models

import android.net.Uri

data class PlaylistVO(
    val id: Int,
    val name: String,
    val artworkUri: Uri?,
    val artworkFilename: String,
    val description: String,
    val trackIds: List<Int>,
)