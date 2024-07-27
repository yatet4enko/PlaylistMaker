package com.practicum.playlistmaker.features.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.ui.dpToPx
import com.practicum.playlistmaker.features.search.data.dto.Track

class PlayerActivity : AppCompatActivity() {

    private val gson = Gson()

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var albumGroup: Group
    private lateinit var artwork: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initToolbar()
        initPlayer()

        try {
            gson.fromJson(intent.getStringExtra("TRACK"), Track::class.java)
        } catch (e: Exception) {
            println("parse track error ${e.stackTrace}")
            null
        }?.let {
            fulfillPlayer(it)
        }
    }

    private fun initPlayer() {
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        duration = findViewById(R.id.duration_value)
        albumGroup = findViewById(R.id.album_group)
        album = findViewById(R.id.album_value)
        year = findViewById(R.id.year_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)
        artwork = findViewById(R.id.artwork)
    }

    private fun initToolbar() {
        findViewById<Toolbar>(R.id.player_toolbar).let {
            title = ""
            setSupportActionBar(it)

            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    fun getCoverArtwork(track: Track) = track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}