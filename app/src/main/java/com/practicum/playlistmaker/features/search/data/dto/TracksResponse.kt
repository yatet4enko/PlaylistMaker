package com.practicum.playlistmaker.features.search.data.dto

data class TracksResponse(
    val resultCount: Int,
    val results: List<TracksResponseItem>
)