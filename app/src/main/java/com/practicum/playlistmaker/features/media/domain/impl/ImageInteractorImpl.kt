package com.practicum.playlistmaker.features.media.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.features.media.domain.api.ImageInteractor
import com.practicum.playlistmaker.features.media.domain.api.ImageRepository

class ImageInteractorImpl constructor(
    private val imageRepository: ImageRepository,
): ImageInteractor {
    override fun saveImage(uri: Uri): String {
        return imageRepository.saveImage(uri)
    }

    override fun getImageUri(path: String): Uri? {
        return imageRepository.getImageUri(path)
    }
}
