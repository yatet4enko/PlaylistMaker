package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksRepositoryImpl(
    private val searchTracksRemoteDataSource: SearchTracksRemoteDataSource,
): SearchTracksRepository {
    override fun search(text: String): Flow<List<Track>?> = flow {
        emit(searchTracksRemoteDataSource.getSearchResults(text))
    }
}