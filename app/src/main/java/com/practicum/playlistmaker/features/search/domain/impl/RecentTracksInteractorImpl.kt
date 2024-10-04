package com.practicum.playlistmaker.features.search.domain.impl

import com.practicum.playlistmaker.features.search.domain.api.RecentTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track

class RecentTracksInteractorImpl(
    private val recentTracksRepository: RecentTracksRepository,
): RecentTracksInteractor {
    override fun clear() {
        recentTracksRepository.clear()
    }

    override fun add(track: Track): List<Track> {
        return recentTracksRepository.add(track)
    }

    override fun getRecentTracks(): List<Track> {
        return recentTracksRepository.getRecentTracks()
    }

}