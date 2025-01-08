package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.media.data.db.AppDatabase
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksRepositoryImpl(
    private val searchTracksRemoteDataSource: SearchTracksRemoteDataSource,
    private val db: AppDatabase,
): SearchTracksRepository {
    override fun search(text: String): Flow<List<Track>?> = flow {
        val favoriteTracksIds = db.trackDao().getAllTrackIds()

        emit(
            searchTracksRemoteDataSource
                .getSearchResults(text)
                ?.map { track ->
                    track.copy(
                        isFavorite = favoriteTracksIds.contains(track.id)
                    )
                }
        )
    }
}