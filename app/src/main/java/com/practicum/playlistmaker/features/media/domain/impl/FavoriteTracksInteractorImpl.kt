package com.practicum.playlistmaker.features.media.domain.impl

import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository,
): FavoriteTracksInteractor {
    override suspend fun add(track: Track) {
        favoriteTracksRepository.add(track)
    }

    override suspend fun remove(track: Track) {
        favoriteTracksRepository.remove(track)
    }

    override suspend fun getAll(): Flow<List<Track>> {
        return favoriteTracksRepository.getAll().map { list ->
            list.reversed().map {
                track -> track.copy(isFavorite = true)
            }
        }
    }

    override suspend fun getAllIds(): Flow<List<Int>> {
        return favoriteTracksRepository.getAllIds()
    }
}
