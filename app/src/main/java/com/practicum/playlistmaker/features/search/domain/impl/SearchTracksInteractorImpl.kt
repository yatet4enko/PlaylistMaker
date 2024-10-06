package com.practicum.playlistmaker.features.search.domain.impl

import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor.TracksConsumer
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository

class SearchTracksInteractorImpl(
    private val searchTracksRepository: SearchTracksRepository,
): SearchTracksInteractor {
    override fun search(text: String, consumer: TracksConsumer) {
        searchTracksRepository.search(text, consumer)
    }
}