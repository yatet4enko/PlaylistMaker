package com.practicum.playlistmaker.features.media.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.ui.SingleLiveEvent
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.features.media.ui.models.FavoriteTracksStateVO
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
): ViewModel(), DefaultLifecycleObserver {

    private val stateLiveData = MutableLiveData<FavoriteTracksStateVO>(FavoriteTracksStateVO.Empty)
    val state: LiveData<FavoriteTracksStateVO> = stateLiveData

    private val navigateTrack = SingleLiveEvent<Track>()
    fun observeNavigateTrack(): LiveData<Track> = navigateTrack

    init {
        actualizeFavorites()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        // Костыли для актуализации данных о добавленных в избранные
        actualizeFavorites()
    }

    fun onTrackClick(track: Track) {
        navigateTrack.postValue(track)
    }

    private fun actualizeFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteTracksInteractor.getAll().collect { favoriteTracks ->
                stateLiveData.postValue(
                    if (favoriteTracks.isEmpty()) FavoriteTracksStateVO.Empty else FavoriteTracksStateVO.Default(favoriteTracks)
                )
            }
        }
    }
}