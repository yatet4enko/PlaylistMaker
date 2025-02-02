package com.practicum.playlistmaker.features.media.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.ui.SingleLiveEvent
import com.practicum.playlistmaker.features.media.domain.api.ImageInteractor
import com.practicum.playlistmaker.features.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import com.practicum.playlistmaker.features.media.ui.models.NewPlaylistStateVO
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val imageInteractor: ImageInteractor,
    private val playlistInteractor: PlaylistInteractor,
): ViewModel() {
    private val stateLiveData = MutableLiveData(
        NewPlaylistStateVO(
            name = "",
            description = "",
            artworkFilename = "",
            artworkUri = null,
            isSubmitButtonDisabled = true,
        )
    )
    val state: LiveData<NewPlaylistStateVO> = stateLiveData

    private val showSuccessToast = SingleLiveEvent<String>()
    fun observeShowSuccessToast(): LiveData<String> = showSuccessToast

    private val close = SingleLiveEvent<Unit>()
    fun observeClose(): LiveData<Unit> = close

    fun onChangeName(name: String) {
        stateLiveData.postValue(stateLiveData.value?.copy(
            name = name,
            isSubmitButtonDisabled = name.isEmpty(),
        ))
    }

    fun onChangeDescription(description: String) {
        stateLiveData.postValue(stateLiveData.value?.copy(
            description = description
        ))
    }

    fun onPickImage(uri: Uri) {
        stateLiveData.postValue(stateLiveData.value?.copy(
            artworkFilename = imageInteractor.saveImage(uri),
            artworkUri = uri,
        ))
    }

    fun onSubmit() {
        val current = state.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.add(Playlist(
                name = current.name,
                description = current.description,
                artworkFilename = current.artworkFilename,
                trackIds = emptyList(),
            ))

            showSuccessToast.postValue(current.name)
            close.postValue(Unit)
        }
    }

    fun isFormEmpty(): Boolean {
        val current = state.value ?: return false

        return current.name.isEmpty() && current.description.isEmpty() && current.artworkFilename.isEmpty()
    }
}