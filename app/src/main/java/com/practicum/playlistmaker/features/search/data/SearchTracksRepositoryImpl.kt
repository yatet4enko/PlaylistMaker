package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor.TracksConsumer
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import java.util.concurrent.Executors

class SearchTracksRepositoryImpl(
    private val searchTracksRemoteDataSource: SearchTracksRemoteDataSource,
): SearchTracksRepository {
    private val executor = Executors.newCachedThreadPool()

    override fun search(text: String, consumer: TracksConsumer) {
        executor.execute {
            consumer.consume(searchTracksRemoteDataSource.getSearchResults(text))
        }
    }
}