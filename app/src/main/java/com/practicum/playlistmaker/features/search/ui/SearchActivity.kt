package com.practicum.playlistmaker.features.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
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

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TracksApi::class.java)

    private val tracks = ArrayList<Track>()
    private val tracksAdapter = SearchResultsAdapter(tracks)

    private lateinit var searchEditText: EditText
    private lateinit var searchResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var notFoundView: View
    private lateinit var errorView: View

    private var searchValue: String = ""

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initToolbar()
        initSearch()
        initSearchResults()
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
        updateSearchIcons(withClose = false)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchValue = s.toString()

                updateSearchIcons(withClose = !s.isNullOrBlank())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
        searchResults = findViewById(R.id.search_results)

        searchResults.adapter = tracksAdapter
    }

    inner class SearchResultsAdapter(
        private val tracks: List<Track>,
    ): RecyclerView.Adapter<SearchResultsAdapter.SearchResultsItemViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SearchResultsItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)

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

    private fun searchTracks(text: String) {
        showLoadingState()

        tracksService.searchTracks(text).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>,
                                    response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {
                    if (response.body()?.results?.isNotEmpty() == true) {
                        val formattedTracks = response.body()?.results!!.map {
                            formatTrack(it)
                        }

                       showSearchResults(formattedTracks)
                    } else {
                        showEmptyState()
                    }
                } else {
                    showErrorState()
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showErrorState()
            }

        })
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
