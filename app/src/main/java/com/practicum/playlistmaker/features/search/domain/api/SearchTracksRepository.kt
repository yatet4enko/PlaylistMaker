package com.practicum.playlistmaker.features.search.domain.api

import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor.TracksConsumer

interface SearchTracksRepository {
    fun search(text: String, consumer: TracksConsumer)
}
