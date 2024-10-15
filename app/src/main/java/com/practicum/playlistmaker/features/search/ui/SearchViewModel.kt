package com.practicum.playlistmaker.features.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.common.ui.SingleLiveEvent
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor.TracksConsumer
import com.practicum.playlistmaker.features.search.domain.models.Track
import com.practicum.playlistmaker.features.search.ui.models.SearchContentStateVO

class SearchViewModel(application: Application): AndroidViewModel(application) {
    private val recentTracksInteractor = getApplication<App>().creator.provideRecentTracksInteractor()
    private val searchTracksInteractor = getApplication<App>().creator.provideSearchTracksInteractor()

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

    private val handler = Handler(Looper.getMainLooper())
    private val searchTracksRunnable = Runnable { searchTracks() }

    private var isClickAllowed = true

    override fun onCleared() {
        super.onCleared()

        handler.removeCallbacks(searchTracksRunnable)
    }

    init {
        updateRecentTracks()
    }

    fun onTextChange(value: String) {
        textLiveData.postValue(value)

        val showRecentTracks = isSearchFocused && value.isEmpty() && recentTracks.isNotEmpty()

        if (showRecentTracks) {
            contentStateLiveData.postValue(SearchContentStateVO.Recent(recentTracks))
        }

        searchTracksDebounced()
    }

    fun onUpdateButtonClick() {
        searchTracks()
    }

    fun onTrackClick(track: Track) {
        if (!clickDebounce()) {
            return
        }

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

    private fun searchTracksDebounced() {
        handler.removeCallbacks(searchTracksRunnable)
        handler.postDelayed(searchTracksRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks() {
        val text = text.value ?: ""
        if (text.isEmpty()) {
            return
        }

        contentStateLiveData.postValue(SearchContentStateVO.Loading)

        searchTracksInteractor.search(text, object :
            TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                if (foundTracks == null) {
                    contentStateLiveData.postValue(SearchContentStateVO.Error)
                    return
                }

                if (foundTracks.isEmpty()) {
                    contentStateLiveData.postValue(SearchContentStateVO.Empty)
                    return
                }

                contentStateLiveData.postValue(SearchContentStateVO.Success(foundTracks))
            }
        })
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun updateRecentTracks() {
        recentTracks.clear()
        recentTracks.addAll(recentTracksInteractor.getRecentTracks())
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
