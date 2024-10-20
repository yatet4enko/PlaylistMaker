package com.practicum.playlistmaker.features.search.ui.models

import com.practicum.playlistmaker.features.search.domain.models.Track

sealed interface SearchContentStateVO {
    object Base: SearchContentStateVO
    object Loading: SearchContentStateVO
    object Empty: SearchContentStateVO
    object Error: SearchContentStateVO
    data class Success(val tracks: List<Track>): SearchContentStateVO
    data class Recent(val tracks: List<Track>): SearchContentStateVO
}
