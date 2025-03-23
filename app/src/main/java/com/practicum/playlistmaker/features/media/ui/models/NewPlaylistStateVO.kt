package com.practicum.playlistmaker.features.media.ui.models

import android.net.Uri

data class NewPlaylistStateVO(
    val name: String,
    val description: String,
    val artworkFilename: String,
    val artworkUri: Uri?,
    val isSubmitButtonDisabled: Boolean,
)
