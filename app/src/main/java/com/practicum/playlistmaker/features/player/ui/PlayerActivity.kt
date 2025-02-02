package com.practicum.playlistmaker.features.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.dpToPx
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.features.media.ui.CreatePlaylistFragment
import com.practicum.playlistmaker.features.media.ui.models.PlaylistVO
import com.practicum.playlistmaker.features.player.ui.models.PlayerState
import com.practicum.playlistmaker.features.player.ui.models.PlayerStateVO
import com.practicum.playlistmaker.features.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    private val viewModel by viewModel<PlayerViewModel>()

    private var binding: ActivityPlayerBinding? = null

    private val gson = Gson()

    private val playlistsAdapter = PlaylistsAdapter {
        onAddToPlaylist(it)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        initToolbarUI()

        viewModel.playerState.observe(this) { state ->
            fulfillPlayer(state)
        }

        viewModel.playlistsState.observe(this) { state ->
            fulfillPlaylists(state)
        }

        viewModel.observeShowTrackInPlaylistToast().observe(this) {
            Toast.makeText(this, getString(R.string.track_already_added_to_playlist, it), Toast.LENGTH_LONG).show()
        }

        viewModel.observeShowTrackAddedToPlaylistToast().observe(this) {
            Toast.makeText(this, getString(R.string.track_added_to_playlist, it), Toast.LENGTH_LONG).show()
        }

        try {
            gson.fromJson(intent.getStringExtra(TRACK_PARAM), Track::class.java)
        } catch (e: Exception) {
            println("parse track error ${e.stackTrace}")
            null
        }?.let {
            viewModel.initialize(it)
        }

        binding?.playButton?.setOnClickListener {
            viewModel.onPlayButtonClick()
        }

        binding?.favButton?.setOnClickListener {
            viewModel.onFavoriteClick()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding!!.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding?.overlay?.visibility = View.GONE
                    }
                    else -> {
                        binding?.overlay?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        viewModel.observeHidePlaylists().observe(this) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding?.addButton?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding?.playlists?.adapter = playlistsAdapter

        binding?.createPlaylist?.setOnClickListener {
            val fragment = CreatePlaylistFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onStop() {
        super.onStop()

        viewModel.onStop()
    }

    private fun initToolbarUI() {
        findViewById<Toolbar>(R.id.player_toolbar).let {
            title = ""
            setSupportActionBar(it)

            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun fulfillPlaylists(state: List<PlaylistVO>) {
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(state)
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun fulfillPlayer(state: PlayerStateVO) {
        state.track?.let { track ->
            binding?.trackName?.text = track.trackName
            binding?.artistName?.text = track.artistName
            binding?.durationValue?.text = track.trackTime
            binding?.yearValue?.text = track.year.toString()
            binding?.genreValue?.text = track.primaryGenreName
            binding?.countryValue?.text = track.country

            if (!track.collectionName.isNullOrEmpty()) {
                binding?.albumValue?.text = track.collectionName
                binding?.albumGroup?.visibility = VISIBLE
            }

            if (track.artworkUrl100.isNotEmpty()) {
                binding?.artwork?.let {
                    Glide
                        .with(this)
                        .load(getCoverArtwork(track))
                        .placeholder(R.drawable.track_placeholder)
                        .error(R.drawable.track_placeholder)
                        .fitCenter()
                        .transform(RoundedCorners(dpToPx(8F, this)))
                        .into(it)
                }
            }

            binding?.favButton?.setImageDrawable(resources.getDrawable(
                if (track.isFavorite) R.drawable.player_fav_active else R.drawable.player_fav
            ))
        }

        binding?.playButton?.isEnabled = state.state !is PlayerState.Default

        binding?.playButton?.setImageDrawable(resources.getDrawable(
            if (state.state is PlayerState.Playing) R.drawable.player_pause else R.drawable.player_play
        ))

        binding?.timing?.text = state.state.progress
    }

    private fun getCoverArtwork(trackDto: Track): String {
        return trackDto.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onAddToPlaylist(playlist: PlaylistVO) {
        viewModel.onAddToPlaylist(playlist)
    }

    companion object {
        const val TRACK_PARAM = "TRACK"
    }
}