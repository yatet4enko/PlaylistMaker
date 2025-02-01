package com.practicum.playlistmaker.features.media.ui.models

import com.practicum.playlistmaker.features.search.domain.models.Track

sealed interface FavoriteTracksStateVO {
    object Empty: FavoriteTracksStateVO
    data class Default(val tracks: List<Track>): FavoriteTracksStateVO
}
