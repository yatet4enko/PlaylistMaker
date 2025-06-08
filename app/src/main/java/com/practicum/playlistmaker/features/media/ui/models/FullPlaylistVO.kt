package com.practicum.playlistmaker.features.media.ui.models

import android.net.Uri
import com.practicum.playlistmaker.features.search.domain.models.Track

data class FullPlaylistVO(
    val id: Int,
    val name: String,
    val description: String,
    val year: String,
    val artworkUri: Uri?,
    val artworkFilename: String,
    val minutesDescription: String,
    val tracksDescription: String,
    val tracks: List<Track>,
)
