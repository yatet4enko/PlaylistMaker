package com.practicum.playlistmaker.features.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.dpToPx
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.features.player.ui.models.PlayerState
import com.practicum.playlistmaker.features.search.domain.models.Track

class PlayerActivity : AppCompatActivity() {
    private val viewModel by viewModels<PlayerViewModel> { PlayerViewModel.getViewModelFactory() }

    private var binding: ActivityPlayerBinding? = null

    private val gson = Gson()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        initToolbarUI()

        viewModel.playerState.observe(this) { state ->
            state.track?.let {
                fulfillPlayer(it)
            }

            binding?.playButton?.isEnabled = state.state != PlayerState.DEFAULT

            binding?.playButton?.setImageDrawable(resources.getDrawable(
                if (state.state == PlayerState.PLAYING) R.drawable.player_pause else R.drawable.player_play
            ))

            binding?.timing?.text = state.timing
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

    private fun fulfillPlayer(track: Track) {
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

    companion object {
        const val TRACK_PARAM = "TRACK"
    }
}