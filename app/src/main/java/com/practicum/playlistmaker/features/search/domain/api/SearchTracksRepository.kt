package com.practicum.playlistmaker.features.search.domain.api

import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksRepository {
    fun search(text: String): Flow<List<Track>?>
}
