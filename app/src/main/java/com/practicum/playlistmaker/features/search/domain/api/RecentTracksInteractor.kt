package com.practicum.playlistmaker.features.search.domain.api

import com.practicum.playlistmaker.features.search.domain.models.Track

interface RecentTracksInteractor {
    fun clear()
    fun add(track: Track): List<Track>
    fun getRecentTracks(): List<Track>
}
