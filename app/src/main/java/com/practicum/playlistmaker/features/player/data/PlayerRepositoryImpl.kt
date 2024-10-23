package com.practicum.playlistmaker.features.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.features.player.domain.api.PlayerRepository

    class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
): PlayerRepository {

    override fun prepare(url: String, consumer: PlayerInteractor.PlayerConsumer) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            consumer.onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            consumer.onCompletion()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentTime(): Int {
        return mediaPlayer.currentPosition
    }
}
