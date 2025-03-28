package com.practicum.playlistmaker.features.player.domain.api

interface PlayerInteractor {
    fun prepare(url: String, consumer: PlayerConsumer)
    fun start()
    fun pause()
    fun release()
    fun getCurrentTime(): Int
    fun isPlaying(): Boolean

    interface PlayerConsumer {
        fun onPrepared()
        fun onCompletion()
    }
}
