package com.practicum.playlistmaker.features.player.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.features.player.domain.api.PlayerInteractor.PlayerConsumer
import com.practicum.playlistmaker.features.player.ui.models.PlayerState
import com.practicum.playlistmaker.features.player.ui.models.PlayerStateVO
import com.practicum.playlistmaker.features.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    application: Application,
    private val playerInteractor: PlayerInteractor
): AndroidViewModel(application) {

    private val playerStateLiveData = MutableLiveData(DEFAULT_STATE)
    val playerState: LiveData<PlayerStateVO> = playerStateLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val updatePlayTimingRunnable = object : Runnable {
        override fun run() {
            updatePlayTiming()

            handler.postDelayed(this, 300)
        }
    }

    override fun onCleared() {
        super.onCleared()

        handler.removeCallbacks(updatePlayTimingRunnable)

        pausePlayer()

        playerInteractor.release()
    }

    fun initialize(track: Track) {
        val currentState = playerStateLiveData.value ?: DEFAULT_STATE

        playerStateLiveData.postValue(currentState.copy(
            track = track,
        ))

        playerInteractor.prepare(track.previewUrl, object : PlayerConsumer {
            override fun onPrepared() {
                playerStateLiveData.postValue(currentState.copy(
                    state = PlayerState.PREPARED,
                ))
            }

            override fun onCompletion() {
                playerStateLiveData.postValue(currentState.copy(
                    state = PlayerState.PREPARED,
                    timing = DEFAULT_TIMING,
                ))

                handler.removeCallbacks(updatePlayTimingRunnable)
            }

        })
    }

    fun onPlayButtonClick() {
        val currentState = (playerState.value ?: DEFAULT_STATE).state

        when(currentState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }

    fun onStop() {
        pausePlayer()
    }

    private fun startPlayer() {
        val currentState = playerStateLiveData.value ?: DEFAULT_STATE

        playerInteractor.start()

        playerStateLiveData.postValue(currentState.copy(
            state = PlayerState.PLAYING,
        ))

        handler.post(updatePlayTimingRunnable)
    }

    private fun pausePlayer() {
        val currentState = playerStateLiveData.value ?: DEFAULT_STATE

        playerInteractor.pause()

        playerStateLiveData.postValue(currentState.copy(
            state = PlayerState.PAUSED,
        ))

        handler.removeCallbacks(updatePlayTimingRunnable)
    }

    private fun updatePlayTiming() {
        val currentState = playerStateLiveData.value ?: DEFAULT_STATE

        playerStateLiveData.postValue(currentState.copy(
            timing = getTimingFromMS(playerInteractor.getCurrentTime())
        ))
    }

    companion object {
        private const val DEFAULT_TIMING = "00:00"

        private val DEFAULT_STATE = PlayerStateVO(
            track = null,
            state = PlayerState.DEFAULT,
            timing = DEFAULT_TIMING,
        )

        private fun getTimingFromMS(ms: Int): String {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(ms)
        }
    }
}