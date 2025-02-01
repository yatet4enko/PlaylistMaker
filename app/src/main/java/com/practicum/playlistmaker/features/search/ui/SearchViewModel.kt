package com.practicum.playlistmaker.features.search.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.ui.SingleLiveEvent
import com.practicum.playlistmaker.common.utils.debounce
import com.practicum.playlistmaker.features.media.domain.api.FavoriteTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.models.Track
import com.practicum.playlistmaker.features.search.ui.models.SearchContentStateVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val recentTracksInteractor: RecentTracksInteractor,
    private val searchTracksInteractor: SearchTracksInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
): AndroidViewModel(application), DefaultLifecycleObserver {
    private val recentTracks = ArrayList<Track>()
    private var isSearchFocused = false

    private val textLiveData = MutableLiveData("")
    val text: LiveData<String> = textLiveData

    private val contentStateLiveData = MutableLiveData<SearchContentStateVO>(SearchContentStateVO.Base)
    val contentState: LiveData<SearchContentStateVO> = contentStateLiveData

    private val navigateTrack = SingleLiveEvent<Track>()
    fun observeNavigateTrack(): LiveData<Track> = navigateTrack

    private val clearSearchInput = SingleLiveEvent<Unit>()
    fun observeClearSearchInput(): LiveData<Unit> = clearSearchInput

    private val hideKeyboard = SingleLiveEvent<Unit>()
    fun observeHideKeyboard(): LiveData<Unit> = hideKeyboard

    private val searchTracksDebounced = debounce<Unit>(
        SEARCH_DEBOUNCE_DELAY_MS,
        viewModelScope,
        true,
    ) {
        searchTracks()
    }

    val onTrackClick = debounce<Track>(
        CLICK_DEBOUNCE_DELAY_MS,
        viewModelScope,
        false,
    ) { track ->
        onTrackClick(track)
    }

    init {
        updateRecentTracks()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        // Костыли для актуализации данных о добавленных в избранные
        actualizeFavorites()
    }

    private fun actualizeFavorites() {
        when (val state = contentStateLiveData.value) {
            // Тут жостко копипаста, потому что хз, как их объединить, он не понимает,
            // что в обоих случаях у меня есть поле tracks и тд
            is SearchContentStateVO.Success -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoriteTracksInteractor.getAllIds().collect { ids ->
                        contentStateLiveData.postValue(
                            state.copy(
                                tracks = state.tracks.map { track ->
                                    track.copy(isFavorite = ids.contains(track.id))
                                }
                            )
                        )
                    }
                }
            }
            is SearchContentStateVO.Recent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoriteTracksInteractor.getAllIds().collect { ids ->
                        contentStateLiveData.postValue(
                            state.copy(
                                tracks = state.tracks.map { track ->
                                    track.copy(isFavorite = ids.contains(track.id))
                                }
                            )
                        )
                    }
                }
            }
            else -> {}
        }
    }

    fun onTextChange(value: String) {
        textLiveData.postValue(value)

        val showRecentTracks = isSearchFocused && value.isEmpty() && recentTracks.isNotEmpty()
        val showBase = isSearchFocused && value.isEmpty()

        if (showRecentTracks) {
            contentStateLiveData.postValue(SearchContentStateVO.Recent(recentTracks))
        } else if (showBase) {
            contentStateLiveData.postValue(SearchContentStateVO.Base)
        }

        searchTracksDebounced(Unit)
    }

    fun onUpdateButtonClick() {
        searchTracks()
    }

    private fun onTrackClick(track: Track) {
        recentTracksInteractor.add(track)
        updateRecentTracks()

        navigateTrack.postValue(track)
    }

    fun onSearhcFocusChange(hasFocus: Boolean) {
        isSearchFocused = hasFocus

        if (hasFocus) {
            val showRecentTracks = (text.value ?: "").isEmpty() && recentTracks.isNotEmpty()

            if (showRecentTracks) {
                contentStateLiveData.postValue(SearchContentStateVO.Recent(recentTracks))
            }
        } else {
            hideKeyboard.postValue(Unit)
        }
    }

    fun onRecentTracksClear() {
        recentTracksInteractor.clear()
        updateRecentTracks()

        contentStateLiveData.postValue(SearchContentStateVO.Base)
    }

    fun onSearchClear() {
        textLiveData.postValue("")
        clearSearchInput.postValue(Unit)

        contentStateLiveData.postValue(SearchContentStateVO.Base)
    }

    private fun searchTracks() {
        val text = text.value ?: ""
        if (text.isEmpty()) {
            return
        }

        contentStateLiveData.postValue(SearchContentStateVO.Loading)

        viewModelScope.launch {
            searchTracksInteractor.search(text)
                .collect { foundTracks ->
                    if (foundTracks == null) {
                        contentStateLiveData.postValue(SearchContentStateVO.Error)
                        return@collect
                    }

                    if (foundTracks.isEmpty()) {
                        contentStateLiveData.postValue(SearchContentStateVO.Empty)
                        return@collect
                    }

                    contentStateLiveData.postValue(SearchContentStateVO.Success(foundTracks))
                }
        }
    }

    private fun updateRecentTracks() {
        recentTracks.clear()

        viewModelScope.launch(Dispatchers.IO) {
            recentTracksInteractor.getRecentTracks().collect { tracks ->
                recentTracks.addAll(tracks)
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 2000L
        private const val CLICK_DEBOUNCE_DELAY_MS = 1000L
    }
}
