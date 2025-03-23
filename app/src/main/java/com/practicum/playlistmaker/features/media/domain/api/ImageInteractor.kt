package com.practicum.playlistmaker.features.media.domain.api

import android.net.Uri

interface ImageInteractor {
    fun saveImage(uri: Uri): String

    fun getImageUri(path: String): Uri?
}
