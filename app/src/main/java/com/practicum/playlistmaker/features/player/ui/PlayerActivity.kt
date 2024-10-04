package com.practicum.playlistmaker.features.player.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.dpToPx
import com.practicum.playlistmaker.features.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

enum class PlayerState {
    DEFAULT,
    PREPARED,
    PLAYING,
    PAUSED,
}

class PlayerActivity : AppCompatActivity() {

    private val gson = Gson()

    private var track: Track? = null

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var albumGroup: Group
    private lateinit var artwork: ImageView
    private lateinit var timing: TextView

    private lateinit var playButton: ImageButton

    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT

    private val updatePlayTimingRunnable = object : Runnable {
        override fun run() {
            updatePlayTiming()

            handler.postDelayed(this, 300)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initToolbarUI()
        initPlayerUI()

        try {
            gson.fromJson(intent.getStringExtra(TRACK_PARAM), Track::class.java)
        } catch (e: Exception) {
            println("parse track error ${e.stackTrace}")
            null
        }?.let {
            track = it
            fulfillPlayer(it)
            initPlayer()
        }
    }

    override fun onStop() {
        super.onStop()

        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(updatePlayTimingRunnable)

        pausePlayer()

        mediaPlayer.release()
    }

    private fun initPlayerUI() {
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        duration = findViewById(R.id.duration_value)
        albumGroup = findViewById(R.id.album_group)
        album = findViewById(R.id.album_value)
        year = findViewById(R.id.year_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)
        artwork = findViewById(R.id.artwork)
        timing = findViewById(R.id.timing)
        playButton = findViewById(R.id.play_button)

        playButton.isEnabled = false
    }

    private fun initToolbarUI() {
        findViewById<Toolbar>(R.id.player_toolbar).let {
            title = ""
            setSupportActionBar(it)

            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initPlayer() {
        track?.let {
            mediaPlayer.setDataSource(it.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.isEnabled = true
                playerState = PlayerState.PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playButton.setImageDrawable(resources.getDrawable(R.drawable.player_play))
                playerState = PlayerState.PREPARED

                handler.removeCallbacks(updatePlayTimingRunnable)

                timing.setText("00:00")
            }
        }

        playButton.setOnClickListener {
            onPlayButtonClick()
        }
    }

    private fun fulfillPlayer(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = track.trackTime
        year.text = track.year.toString()
        genre.text = track.primaryGenreName
        country.text = track.country

        if (!track.collectionName.isNullOrEmpty()) {
            album.text = track.collectionName
            albumGroup.visibility = VISIBLE
        }

        if (track.artworkUrl100.isNotEmpty()) {
            Glide
                .with(this)
                .load(getCoverArtwork(track))
                .placeholder(R.drawable.track_placeholder)
                .error(R.drawable.track_placeholder)
                .fitCenter()
                .transform(RoundedCorners(dpToPx(8F, this)))
                .into(artwork)
        }
    }

    fun getCoverArtwork(trackDto: Track) = trackDto.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()

        playButton.setImageDrawable(resources.getDrawable(R.drawable.player_pause))

        playerState = PlayerState.PLAYING

        handler.post(updatePlayTimingRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()

        playButton.setImageDrawable(resources.getDrawable(R.drawable.player_play))

        playerState = PlayerState.PAUSED

        handler.removeCallbacks(updatePlayTimingRunnable)
    }

    private fun onPlayButtonClick() {
        when(playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            else -> {}
        }
    }

    private fun updatePlayTiming() {
        timing.setText(getTimingFromMS(mediaPlayer.currentPosition))
    }

    companion object {
        const val TRACK_PARAM = "TRACK"

        private fun getTimingFromMS(ms: Int): String {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(ms)
        }
    }
}