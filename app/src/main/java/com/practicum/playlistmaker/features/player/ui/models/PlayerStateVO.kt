package com.practicum.playlistmaker.features.player.ui.models

import com.practicum.playlistmaker.features.search.domain.models.Track

data class PlayerStateVO(
    val track: Track?,
    val state: PlayerState,
    val timing: String,
)
