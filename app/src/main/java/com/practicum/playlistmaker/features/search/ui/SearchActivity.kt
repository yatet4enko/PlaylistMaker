package com.practicum.playlistmaker.features.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View.INVISIBLE
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.features.player.ui.PlayerActivity
import com.practicum.playlistmaker.features.player.ui.PlayerActivity.Companion.TRACK_PARAM
import com.practicum.playlistmaker.features.search.domain.models.Track
import com.practicum.playlistmaker.features.search.ui.models.SearchContentStateVO
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var binding: ActivitySearchBinding

    private val tracksAdapter = SearchResultsAdapter {
        viewModel.onTrackClick(it)
    }
    private val recentTracksAdapter = SearchResultsAdapter {
        viewModel.onTrackClick(it)
    }

    private val gson = Gson()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel.contentState.observe(this) {
            renderContent(it)
        }

        viewModel.text.observe(this) {
            val withClose = it.isNotEmpty()

            binding.searchEditText.setCompoundDrawablesWithIntrinsicBounds(
                getDrawable(R.drawable.search_icon),
                null,
                if (withClose) getDrawable(R.drawable.close) else null,
                null
            )
        }

        viewModel.observeClearSearchInput().observe(this) {
            binding.searchEditText.setText("")
        }

        viewModel.observeNavigateTrack().observe(this) {
            val intent = Intent(this, PlayerActivity::class.java)

            intent.putExtra(TRACK_PARAM, gson.toJson(it))

            startActivity(intent)
        }

        viewModel.observeHideKeyboard().observe(this) {
            hideKeyboard()
        }

        initToolbar()
        initSearch()
        initSearchResults()
        initSearchRecent()
    }

    private fun renderContent(state: SearchContentStateVO) {
        when (state) {
            is SearchContentStateVO.Base -> showBaseView()
            is SearchContentStateVO.Loading -> showLoadingState()
            is SearchContentStateVO.Empty -> showEmptyState()
            is SearchContentStateVO.Error -> showErrorState()
            is SearchContentStateVO.Success -> showSearchResults(state.tracks)
            is SearchContentStateVO.Recent -> showRecentTracks(state.tracks)
        }
    }

    private fun initToolbar() {
        binding.toolbar.let {
            title = ""
            setSupportActionBar(it)
            title = resources.getString(R.string.search)

            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initSearch() {
        binding.updateButton.setOnClickListener {
            viewModel.onUpdateButtonClick()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            viewModel.onSearhcFocusChange(hasFocus)
        }

        binding.searchEditText.setOnTouchListener(OnTouchListener { v, event ->
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

    private fun onSearchClear() {
        binding.searchEditText.clearFocus()

        viewModel.onSearchClear()
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun initSearchResults() {
        binding.searchResults.adapter = tracksAdapter
    }

    private fun initSearchRecent() {
        binding.recentTracksItems.adapter = recentTracksAdapter

        binding.clearRecent.setOnClickListener {
            viewModel.onRecentTracksClear()
        }
    }

    private fun showRecentTracks(tracks: List<Track>) {
        recentTracksAdapter.tracks.clear()
        recentTracksAdapter.tracks.addAll(tracks)
        recentTracksAdapter.notifyDataSetChanged()

        binding.progress.visibility = INVISIBLE
        binding.searchResults.visibility = INVISIBLE
        binding.notFound.visibility = INVISIBLE
        binding.error.visibility = INVISIBLE
        binding.recentTracks.visibility = VISIBLE
    }

    private fun showSearchResults(newTracks: List<Track>) {
        tracksAdapter.tracks.clear()
        tracksAdapter.tracks.addAll(newTracks)
        tracksAdapter.notifyDataSetChanged()

        binding.progress.visibility = INVISIBLE
        binding.searchResults.visibility = VISIBLE
        binding.notFound.visibility = INVISIBLE
        binding.error.visibility = INVISIBLE
        binding.recentTracks.visibility = INVISIBLE
    }

    private fun showErrorState() {
        binding.progress.visibility = INVISIBLE
        binding.searchResults.visibility = INVISIBLE
        binding.notFound.visibility = INVISIBLE
        binding.error.visibility = VISIBLE
        binding.recentTracks.visibility = INVISIBLE
    }

    private fun showEmptyState() {
        binding.progress.visibility = INVISIBLE
        binding.searchResults.visibility = INVISIBLE
        binding.notFound.visibility = VISIBLE
        binding.error.visibility = INVISIBLE
        binding.recentTracks.visibility = INVISIBLE
    }

    private fun showLoadingState() {
        binding.progress.visibility = VISIBLE
        binding.searchResults.visibility = INVISIBLE
        binding.notFound.visibility = INVISIBLE
        binding.error.visibility = INVISIBLE
        binding.recentTracks.visibility = INVISIBLE
    }

    private fun showBaseView() {
        binding.progress.visibility = INVISIBLE
        binding.searchResults.visibility = INVISIBLE
        binding.notFound.visibility = INVISIBLE
        binding.error.visibility = INVISIBLE
        binding.recentTracks.visibility = INVISIBLE
    }
}
