package com.practicum.playlistmaker.features.player.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.ui.SingleLiveEvent
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.features.media.domain.api.ImageInteractor
import com.practicum.playlistmaker.features.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import com.practicum.playlistmaker.features.media.ui.models.PlaylistVO
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor.PlayerConsumer
import com.practicum.playlistmaker.features.player.ui.models.PlayerState
import com.practicum.playlistmaker.features.player.ui.models.PlayerStateVO
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    application: Application,
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val imageInteractor: ImageInteractor,
): AndroidViewModel(application) {

    private val playerStateLiveData = MutableLiveData(DEFAULT_STATE)
    val playerState: LiveData<PlayerStateVO> = playerStateLiveData

    private val playlistsStateLiveData: MutableLiveData<List<PlaylistVO>> = MutableLiveData(emptyList())
    val playlistsState: LiveData<List<PlaylistVO>> = playlistsStateLiveData

    private val showTrackInPlaylistToast = SingleLiveEvent<String>()
    fun observeShowTrackInPlaylistToast(): LiveData<String> = showTrackInPlaylistToast

    private val showTrackAddedToPlaylistToast = SingleLiveEvent<String>()
    fun observeShowTrackAddedToPlaylistToast(): LiveData<String> = showTrackAddedToPlaylistToast

    private val hidePlaylists = SingleLiveEvent<Unit>()
    fun observeHidePlaylists(): LiveData<Unit> = hidePlaylists

    private var isInitialized = false

    private var updateTimingJob: Job? = null

    override fun onCleared() {
        super.onCleared()

        pausePlayer()

        playerInteractor.release()
    }

    fun initialize(track: Track) {
        if (isInitialized) {
            return
        }

        isInitialized = true

        playerStateLiveData.postValue(getCurrentState().copy(
            track = track,
        ))

        track.previewUrl?.let {
            playerInteractor.prepare(it, object : PlayerConsumer {
                override fun onPrepared() {
                    playerStateLiveData.postValue(getCurrentState().copy(
                        state = PlayerState.Prepared(),
                    ))
                }

                override fun onCompletion() {
                    playerStateLiveData.postValue(getCurrentState().copy(
                        state = PlayerState.Prepared(),
                    ))
                }

            })
        }

        viewModelScope.launch {
            playlistInteractor.getAll()
                .collect { playlists ->
                    playlistsStateLiveData.postValue(
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

    fun onAddToPlaylist(playlistVO: PlaylistVO) {
        viewModelScope.launch {
            val trackId = playerState.value?.track?.id ?: return@launch

            if (playlistVO.trackIds.contains(trackId)) {
                showTrackInPlaylistToast.postValue(playlistVO.name)

                return@launch
            }

            playlistInteractor.addTrackToPlaylist(
                Playlist(
                    id = playlistVO.id,
                    name = playlistVO.name,
                    description = playlistVO.description,
                    artworkFilename = playlistVO.artworkFilename,
                    trackIds = playlistVO.trackIds,
                ),
                trackId,
            )

            playlistsStateLiveData.postValue(
                playlistsStateLiveData.value?.map {
                    if (it.id == playlistVO.id) {
                        it.copy(
                            trackIds = it.trackIds + trackId
                        )
                    } else {
                        it
                    }
                }
            )

            showTrackAddedToPlaylistToast.postValue(playlistVO.name)

            hidePlaylists.postValue(Unit)

        }
    }

    fun onFavoriteClick() {
        playerStateLiveData.value?.track?.let { track ->
            if (track.isFavorite) {
                viewModelScope.launch(Dispatchers.IO) {
                    favoriteTracksInteractor.remove(track)
                }
            } else {
                viewModelScope.launch((Dispatchers.IO)) {
                    favoriteTracksInteractor.add(track)
                }
            }

            val currentState = playerStateLiveData.value ?: DEFAULT_STATE

            playerStateLiveData.postValue(currentState.copy(
                track = track.copy(
                    isFavorite = !track.isFavorite
                )
            ))
        }
    }

    fun onPlayButtonClick() {
        val currentState = (playerState.value ?: DEFAULT_STATE).state

        when(currentState) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> {}
        }
    }

    fun onStop() {
        pausePlayer()
    }

    private fun getCurrentState(): PlayerStateVO {
        return playerStateLiveData.value ?: DEFAULT_STATE
    }

    private fun startPlayer() {
        val currentState = playerStateLiveData.value ?: DEFAULT_STATE

        playerInteractor.start()

        playerStateLiveData.postValue(currentState.copy(
            state = PlayerState.Playing(getCurrentPlayerPosition()),
        ))

        startUpdatePlayTiming()
    }

    private fun pausePlayer() {
        val currentState = playerStateLiveData.value ?: DEFAULT_STATE

        playerInteractor.pause()

        playerStateLiveData.postValue(currentState.copy(
            state = PlayerState.Paused(getCurrentPlayerPosition()),
        ))

        stopUpdatePlayTiming()
    }

    private fun startUpdatePlayTiming() {
        updateTimingJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                updatePlayTiming()

                delay(300)
            }
        }
    }

    private fun stopUpdatePlayTiming() {
        updateTimingJob?.cancel()
    }

    private fun updatePlayTiming() {
        val currentState = playerStateLiveData.value ?: DEFAULT_STATE

        playerStateLiveData.postValue(currentState.copy(
            state = PlayerState.Playing(getCurrentPlayerPosition()),
        ))
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentTime()) ?: "00:00"
    }

    companion object {
        private val DEFAULT_STATE = PlayerStateVO(
            track = null,
            state = PlayerState.Default(),
        )
    }
}