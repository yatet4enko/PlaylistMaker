package com.practicum.playlistmaker.features.player.domain.api

import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor.PlayerConsumer

interface PlayerRepository {
    fun prepare(url: String, consumer: PlayerConsumer)
    fun start()
    fun pause()
    fun release()
    fun getCurrentTime(): Int
}
