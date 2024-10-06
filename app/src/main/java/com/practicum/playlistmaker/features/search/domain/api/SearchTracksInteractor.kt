package com.practicum.playlistmaker.features.search.domain.api

import com.practicum.playlistmaker.features.search.domain.models.Track

interface SearchTracksInteractor {
    fun search(text: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}
