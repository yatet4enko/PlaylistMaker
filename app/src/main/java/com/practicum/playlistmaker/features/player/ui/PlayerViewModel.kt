package com.practicum.playlistmaker.features.player.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor.PlayerConsumer
import com.practicum.playlistmaker.features.player.ui.models.PlayerState
import com.practicum.playlistmaker.features.player.ui.models.PlayerStateVO
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    application: Application,
    private val playerInteractor: PlayerInteractor
): AndroidViewModel(application) {

    private val playerStateLiveData = MutableLiveData(DEFAULT_STATE)
    val playerState: LiveData<PlayerStateVO> = playerStateLiveData

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

        playerInteractor.prepare(track.previewUrl, object : PlayerConsumer {
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

                Log.i("GGWP", "111")

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