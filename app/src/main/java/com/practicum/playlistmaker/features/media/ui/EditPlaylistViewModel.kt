package com.practicum.playlistmaker.features.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.ui.SingleLiveEvent
import com.practicum.playlistmaker.features.media.domain.api.ImageInteractor
import com.practicum.playlistmaker.features.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import com.practicum.playlistmaker.features.media.ui.models.NewPlaylistStateVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val imageInteractor: ImageInteractor,
    private val playlistInteractor: PlaylistInteractor,
): CreatePlaylistViewModel(imageInteractor, playlistInteractor) {

    private var playlist: Playlist? = null

    private val initForm = SingleLiveEvent<NewPlaylistStateVO>()
    fun observeInitForm(): LiveData<NewPlaylistStateVO> = initForm

    fun loadPlaylist(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylist(id).first().let { playlistDomain ->
                playlistDomain ?: return@launch

                playlist = playlistDomain

                val name = playlistDomain.name
                val description = playlistDomain.description
                val artworkFileName = playlistDomain.artworkFilename
                val artworkUrl = if (playlistDomain.artworkFilename.isEmpty()) {
                    null
                } else {
                    imageInteractor.getImageUri(playlistDomain.artworkFilename)
                }

                val vo = NewPlaylistStateVO(
                    name = name,
                    description = description,
                    artworkFilename = artworkFileName,
                    artworkUri = artworkUrl,
                    isSubmitButtonDisabled = false,
                )

                stateLiveData.postValue(vo)

                initForm.postValue(vo)
            }
        }
    }

    override fun onSubmit() {
        val formData = state.value ?: return
        val playlistData = playlist ?: return


        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.updatePlaylist(playlistData.copy(
                name = formData.name,
                description = formData.description,
                artworkFilename = formData.artworkFilename,
            ))

            close.postValue(Unit)
        }
    }
}