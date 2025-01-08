package com.practicum.playlistmaker.features.search.domain.impl

import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class SearchTracksInteractorImpl(
    private val searchTracksRepository: SearchTracksRepository,
): SearchTracksInteractor {
    override fun search(text: String): Flow<List<Track>?> {
        return searchTracksRepository.search(text)
    }
}
