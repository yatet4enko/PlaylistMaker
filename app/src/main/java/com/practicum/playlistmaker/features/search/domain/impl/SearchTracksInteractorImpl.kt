package com.practicum.playlistmaker.features.search.domain.impl

import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor.TracksConsumer
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import java.util.concurrent.Executors

class SearchTracksInteractorImpl(
    private val searchTracksRepository: SearchTracksRepository,
): SearchTracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun search(text: String, consumer: TracksConsumer) {
        executor.execute{
            consumer.consume(searchTracksRepository.search(text))
        }
    }
}