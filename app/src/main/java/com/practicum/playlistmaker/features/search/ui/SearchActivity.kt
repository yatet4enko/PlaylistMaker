package com.practicum.playlistmaker.features.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.features.player.ui.PlayerActivity
import com.practicum.playlistmaker.features.player.ui.PlayerActivity.Companion.TRACK_PARAM
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksInteractor.TracksConsumer
import com.practicum.playlistmaker.features.search.domain.models.Track

class SearchActivity : AppCompatActivity() {
    private lateinit var recentTracksInteractor: RecentTracksInteractor
    private lateinit var searchTracksInteractor: SearchTracksInteractor

    private val gson = Gson()

    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    private val searchTracksRunnable = Runnable { searchTracks() }

    private val tracks = ArrayList<Track>()
    private val tracksAdapter = SearchResultsAdapter(tracks) { track ->
        onTrackClick(track)
    }

    private lateinit var recentTracks: ArrayList<Track>
    private lateinit var recentTracksAdapter:  SearchResultsAdapter

    private lateinit var searchResultsView: FrameLayout

    private lateinit var searchEditText: EditText
    private lateinit var searchResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var notFoundView: View
    private lateinit var errorView: View
    private lateinit var updateButton: Button
    private lateinit var recentTracksView: FrameLayout
    private lateinit var recentTracksItems: RecyclerView
    private lateinit var clearRecentButton: Button

    private var searchValue: String = ""

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recentTracksInteractor = (applicationContext as App).creator.provideRecentTracksInteractor()
        searchTracksInteractor = (applicationContext as App).creator.provideSearchTracksInteractor()

        initToolbar()
        initSearch()
        initSearchResults()
        initSearchRecent()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SEARCH_VALUE_KEY, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchEditText.setText(searchValue)
    }

    private fun initToolbar() {
        findViewById<Toolbar>(R.id.toolbar).let {
            title = ""
            setSupportActionBar(it)
            title = resources.getString(R.string.search)

            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initSearch() {
        notFoundView = findViewById(R.id.not_found)
        errorView = findViewById(R.id.error)
        progressBar = findViewById(R.id.progress)
        searchEditText = findViewById(R.id.search_edit_text)
        updateButton = findViewById(R.id.update_button)
        updateSearchIcons(withClose = false)

        updateButton.setOnClickListener {
            searchTracks()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchValue = s.toString()

                updateSearchIcons(withClose = !s.isNullOrBlank())

                val isRecentTracksVisible = searchEditText.hasFocus() && searchValue.isEmpty() && recentTracks.isNotEmpty()

                setRecentTracksVisible(isRecentTracksVisible)

                searchTracksDebounced()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            val isRecentTracksVisible = hasFocus && searchEditText.text.isEmpty() && recentTracks.isNotEmpty()

            setRecentTracksVisible(isRecentTracksVisible)
        }

        searchEditText.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val textView = v as TextView
                if (event.x >= textView.width - textView.compoundPaddingEnd) {
                    onSearchClear()
                    return@OnTouchListener true
                }
            }
            false
        })
    }


    private fun updateSearchIcons(withClose: Boolean) {
        searchEditText.setCompoundDrawablesWithIntrinsicBounds(
            getDrawable(R.drawable.search_icon),
            null,
            if (withClose) getDrawable(R.drawable.close) else null,
            null
        )
    }

    private fun onSearchClear() {
        searchEditText.setText("")
        searchEditText.clearFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)

        showBaseView()
    }

    private fun initSearchResults() {
        searchResultsView = findViewById(R.id.search_results_view)
        searchResults = findViewById(R.id.search_results)

        searchResults.adapter = tracksAdapter
    }

    private fun initSearchRecent() {
        clearRecentButton = findViewById(R.id.clear_recent)

        recentTracks = ArrayList(recentTracksInteractor.getRecentTracks())
        recentTracksAdapter = SearchResultsAdapter(recentTracks) { track ->
            onTrackClick(track)
        }

        recentTracksView = findViewById(R.id.recent_tracks)

        recentTracksItems = findViewById(R.id.recent_tracks_items)

        recentTracksItems.adapter = recentTracksAdapter

        clearRecentButton.setOnClickListener {
            recentTracksInteractor.clear()

            onChangeRecentTracks(emptyList())

            setRecentTracksVisible(false)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        if (!clickDebounce()) {
            return
        }

        onChangeRecentTracks(recentTracksInteractor.add(track))

        val intent = Intent(this@SearchActivity, PlayerActivity::class.java)

        intent.putExtra(TRACK_PARAM, gson.toJson(track))

        startActivity(intent)
    }

    private fun searchTracksDebounced() {
        handler.removeCallbacks(searchTracksRunnable)
        handler.postDelayed(searchTracksRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks() {
        showLoadingState()

        searchTracksInteractor.search(searchEditText.text.toString(), object : TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                if (foundTracks == null) {
                    runOnUiThread {
                        showErrorState()
                    }
                    return
                }

                if (foundTracks.isEmpty()) {
                    runOnUiThread {
                        showEmptyState()
                    }
                    return
                }

                runOnUiThread {
                    showSearchResults(foundTracks)
                }
            }
        })
    }

    private fun setRecentTracksVisible(isVisilbe: Boolean) {
        recentTracksView.visibility = if (isVisilbe) VISIBLE else INVISIBLE

        searchResultsView.visibility = if (!isVisilbe) VISIBLE else INVISIBLE
    }

    private fun onChangeRecentTracks(tracks: List<Track>) {
        recentTracks.clear()
        recentTracks.addAll(tracks)
        recentTracksAdapter.notifyDataSetChanged()
    }

    private fun showSearchResults(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        tracksAdapter.notifyDataSetChanged()

        progressBar.visibility = INVISIBLE
        searchResults.visibility = VISIBLE
        notFoundView.visibility = INVISIBLE
        errorView.visibility = INVISIBLE
    }

    private fun showErrorState() {
        progressBar.visibility = INVISIBLE
        searchResults.visibility = INVISIBLE
        notFoundView.visibility = INVISIBLE
        errorView.visibility = VISIBLE
    }

    private fun showEmptyState() {
        progressBar.visibility = INVISIBLE
        searchResults.visibility = INVISIBLE
        notFoundView.visibility = VISIBLE
        errorView.visibility = INVISIBLE
    }

    private fun showLoadingState() {
        progressBar.visibility = VISIBLE
        searchResults.visibility = INVISIBLE
        notFoundView.visibility = INVISIBLE
        errorView.visibility = INVISIBLE
    }

    private fun showBaseView() {
        progressBar.visibility = INVISIBLE
        searchResults.visibility = INVISIBLE
        notFoundView.visibility = INVISIBLE
        errorView.visibility = INVISIBLE
    }

    companion object {
        private const val SEARCH_VALUE_KEY = "SEARCH_VALUE_KEY"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
