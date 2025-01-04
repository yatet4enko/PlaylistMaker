package com.practicum.playlistmaker.features.player.domain.impl

import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.features.player.domain.api.PlayerRepository

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository,
): PlayerInteractor {
    override fun prepare(url: String, consumer: PlayerInteractor.PlayerConsumer) {
        playerRepository.prepare(url, consumer)
    }

    override fun start() {
        playerRepository.start()
    }

    override fun pause() {
        playerRepository.pause()
    }

    override fun release() {
        playerRepository.release()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

    override fun getCurrentTime(): Int {
        return playerRepository.getCurrentTime()
    }
}