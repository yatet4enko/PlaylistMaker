package com.practicum.playlistmaker.features.search.domain.api

import com.practicum.playlistmaker.features.search.domain.models.Track

interface SearchTracksRepository {
    fun search(text: String): List<Track>?
}
