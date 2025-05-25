package com.practicum.playlistmaker.features.media.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.SingleLiveEvent
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.features.media.domain.api.ImageInteractor
import com.practicum.playlistmaker.features.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.features.media.ui.models.FullPlaylistVO
import com.practicum.playlistmaker.features.search.domain.models.Track
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

class PlaylistViewModel(
    private val application: Application,
    private val playlistInteractor: PlaylistInteractor,
    private val imageInteractor: ImageInteractor,
    private val tracksInteractor: FavoriteTracksInteractor,
): AndroidViewModel(application) {

    private val _data: MutableLiveData<FullPlaylistVO?> = MutableLiveData(null)
    val data: LiveData<FullPlaylistVO?> = _data

    private val showShareDialog = SingleLiveEvent<String>()
    fun observeShowShareDialog(): LiveData<String> = showShareDialog

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private val close = SingleLiveEvent<Unit>()
    fun observeClose(): LiveData<Unit> = close

    fun loadPlaylist(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylist(id).combine(tracksInteractor.getAll()) { playlistDomain, allTracks ->
                Pair(playlistDomain, allTracks)
            }.collect {
                data ->
                val playlistDomain = data.first
                val allTracks = data.second

                playlistDomain ?: return@collect

                val tracks = playlistDomain.trackIds.map { id ->
                    allTracks.firstOrNull { it.id == id }
                }.filterNotNull()

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = playlistDomain.createdAt
                val year = calendar.get(Calendar.YEAR)

                val totalMillis = tracks.sumOf { it.trackTimeMillis }
                val totalMunites = totalMillis / (1000 * 60)

                _data.value = FullPlaylistVO(
                    id = playlistDomain.id!!,
                    name = playlistDomain.name,
                    year = year.toString(),
                    artworkFilename = playlistDomain.artworkFilename,
                    artworkUri = if (playlistDomain.artworkFilename.isEmpty()) {
                        null
                    } else {
                        imageInteractor.getImageUri(playlistDomain.artworkFilename)
                    },
                    minutesDescription = application.resources.getQuantityString(
                        R.plurals.n_minutes,
                        totalMunites,
                        totalMunites,
                    ),
                    tracksDescription = application.resources.getQuantityString(
                        R.plurals.n_tracks,
                        tracks.size,
                        tracks.size,
                    ),
                    description = playlistDomain.description,
                    tracks = tracks,
                )
            }

        }
    }

    fun onDeleteTrack(track: Track) {
        viewModelScope.launch {
            data.value?.let { playlist ->
                playlistInteractor.removeTrackFromPlaylist(playlist.id, track.id)

                loadPlaylist(playlist.id)
            }
        }
    }

    fun onSharePlaylistClick() {
        val playlist = _data.value ?: return
        val tracks = playlist.tracks

        if (tracks.isNotEmpty()) {
            showShareDialog.postValue(formatPlaylistMessage(playlist))
        } else {
            showToast.postValue(application.resources.getString(R.string.no_tracks))
        }
    }

    fun onDeletePlaylistClick() {
        val playlist = _data.value ?: return


        viewModelScope.launch {
            playlistInteractor.removePlaylist(playlist.id)

            close.postValue(Unit)
        }
    }

    private fun formatPlaylistMessage(playlist: FullPlaylistVO): String {
        val trackCount = playlist.tracks.size
        val trackList = playlist.tracks
            .mapIndexed { index, track ->
                "${index + 1}. ${track.artistName} - ${track.trackName} (${formatTrackTime(track.trackTimeMillis)})"
            }
            .joinToString("\n")

        return """
        ${playlist.name}
        ${playlist.description}
        [$trackCount] треков
        $trackList
    """.trimIndent()
    }

    private fun formatTrackTime(trackTimeMillis: Int): String {
        val seconds = (trackTimeMillis / 1000) % 60
        val minutes = (trackTimeMillis / 1000) / 60
        return "%02d:%02d".format(minutes, seconds)
    }

}