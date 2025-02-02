package com.practicum.playlistmaker.features.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.features.media.domain.api.ImageInteractor
import com.practicum.playlistmaker.features.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.features.media.ui.models.PlaylistVO
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val imageInteractor: ImageInteractor,
): ViewModel() {

    private val stateLiveData: MutableLiveData<List<PlaylistVO>> = MutableLiveData(emptyList())
    val state: LiveData<List<PlaylistVO>> = stateLiveData

    init {
        viewModelScope.launch {
            playlistInteractor.getAll()
                .collect { playlists ->
                    stateLiveData.postValue(
                        playlists.map {
                            PlaylistVO(
                                id = it.id ?: 0,
                                name = it.name,
                                description = it.description,
                                trackIds = it.trackIds,
                                artworkFilename = it.artworkFilename,
                                artworkUri = if (it.artworkFilename.isEmpty()) {
                                    null
                                } else {
                                    imageInteractor.getImageUri(it.artworkFilename)
                                }
                            )
                        }
                    )
                }
        }
    }

}
