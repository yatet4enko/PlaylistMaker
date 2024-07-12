package com.practicum.playlistmaker.features.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.dpToPx
import com.practicum.playlistmaker.features.search.data.dto.Track
import com.practicum.playlistmaker.features.search.data.dto.TracksResponse
import com.practicum.playlistmaker.features.search.data.dto.TracksResponseItem
import com.practicum.playlistmaker.features.search.data.repository.RecentTracksRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Locale

interface TracksApi {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TracksResponse>
}

class SearchActivity : AppCompatActivity() {
    private val recentTracksRepository = RecentTracksRepository(this)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TracksApi::class.java)

    private val tracks = ArrayList<Track>()
    private val tracksAdapter = SearchResultsAdapter(tracks)

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

//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        if (ev.action == MotionEvent.ACTION_DOWN) {
//            val v = currentFocus
//            if (v is EditText) {
//                val outRect = Rect()
//                v.getGlobalVisibleRect(outRect)
//                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
//                    v.clearFocus()
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(v.windowToken, 0)
//                }
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }

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
            searchTracks(searchEditText.text.toString())
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchValue = s.toString()

                updateSearchIcons(withClose = !s.isNullOrBlank())

                val isRecentTracksVisible = searchEditText.hasFocus() && searchValue.isEmpty() && recentTracks.isNotEmpty()

                setRecentTracksVisible(isRecentTracksVisible)
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

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchEditText.text.toString())
                true
            }
            false
        }
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

        recentTracks = ArrayList(recentTracksRepository.getRecentTracks())
        recentTracksAdapter = SearchResultsAdapter(recentTracks)

        recentTracksView = findViewById(R.id.recent_tracks)

        recentTracksItems = findViewById(R.id.recent_tracks_items)

        recentTracksItems.adapter = recentTracksAdapter

        clearRecentButton.setOnClickListener {
            recentTracksRepository.clear()

            onChangeRecentTracks(emptyList())

            setRecentTracksVisible(false)
        }
    }

    inner class SearchResultsAdapter(
        private val tracks: List<Track>,
    ): RecyclerView.Adapter<SearchResultsAdapter.SearchResultsItemViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SearchResultsItemViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.track_view, parent, false)

            return SearchResultsItemViewHolder(view)
        }

        override fun getItemCount(): Int {
            return tracks.size
        }

        override fun onBindViewHolder(holder: SearchResultsItemViewHolder, position: Int) {
            holder.bind(tracks[position])
        }

        inner class SearchResultsItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            private val artwork: ImageView = itemView.findViewById(R.id.track_artwork)
            private val title: TextView = itemView.findViewById(R.id.track_title)
            private val description: TextView = itemView.findViewById(R.id.track_description)

            fun bind(track: Track) {
                title.text = track.trackName
                description.text = "${track.artistName}  •  ${track.trackTime}"

                itemView.setOnClickListener {
                    onTrackClick(track)
                }

                Glide
                    .with(itemView)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.track_placeholder)
                    .error(R.drawable.track_placeholder)
                    .fitCenter()
                    .transform(RoundedCorners(dpToPx(2F, itemView.context)))
                    .into(artwork)
            }
        }
    }

    private fun onTrackClick(track: Track) {
        onChangeRecentTracks(recentTracksRepository.addRecentTrack(track))
    }


    private fun searchTracks(text: String) {
        showLoadingState()

        tracksService.searchTracks(text).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                if (response.code() != 200) {
                    showErrorState()
                    return
                }

                val results = response.body()?.results
                if (results?.isNotEmpty() == true) {
                    val formattedTracks = results.map {
                        formatTrack(it)
                    }

                    showSearchResults(formattedTracks)
                } else {
                    showEmptyState()
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showErrorState()
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

    private fun formatTrack(data: TracksResponseItem): Track {
        val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(data.trackTimeMillis)

        return Track(
            id = data.trackId,
            trackName = data.trackName,
            artistName = data.artistName,
            trackTime = trackTime,
            artworkUrl100 = data.artworkUrl100
        )
    }


    companion object {
        private const val SEARCH_VALUE_KEY = "SEARCH_VALUE_KEY"
    }
}
