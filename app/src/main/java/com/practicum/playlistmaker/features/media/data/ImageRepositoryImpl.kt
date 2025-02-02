package com.practicum.playlistmaker.features.media.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.features.media.domain.api.ImageRepository
import java.io.File
import java.io.FileOutputStream
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.core.net.toUri
import java.util.UUID

class ImageRepositoryImpl(
    private val context: Context
): ImageRepository {
    override fun saveImage(uri: Uri): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) filePath.mkdirs()

        val file = File(filePath, generateRandomFilename())

        val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
        val outputStream = FileOutputStream(file)

        BitmapFactory.decodeStream(inputStream)?.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        inputStream.close()
        outputStream.close()

        return file.absolutePath
    }

    override fun getImageUri(path: String): Uri {
        val file = File(path)

        return file.toUri()
    }

    private fun generateRandomFilename(): String {
        return UUID.randomUUID().toString()
    }
}