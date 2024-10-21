package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor.TracksConsumer
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import java.util.concurrent.ExecutorService

class SearchTracksRepositoryImpl(
    private val searchTracksRemoteDataSource: SearchTracksRemoteDataSource,
    private val executor: ExecutorService,
): SearchTracksRepository {
    override fun search(text: String, consumer: TracksConsumer) {
        executor.execute {
            consumer.consume(searchTracksRemoteDataSource.getSearchResults(text))
        }
    }
}