package com.practicum.playlistmaker.features.search.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.features.search.data.dto.Track

class RecentTracksRepository(
    private val context: Context,
) {
    var preferences: SharedPreferences? = null

    private val gson = Gson()

    fun clear() {
        getSharedPreferences(context)
            .edit()
            .remove(SHARED_PREFERENCES_RECENT_TRACKS_KEY,)
            .apply()
    }

    fun addRecentTrack(track: Track): List<Track> {
        val recentTracks = getRecentTracks()
            .filter { it.id != track.id }
            .toMutableList()

        recentTracks.add(0, track)

        if (recentTracks.size > MAX_COUNT) {
            recentTracks.removeLast()
        }

        getSharedPreferences(context)
            .edit()
            .putString(
                SHARED_PREFERENCES_RECENT_TRACKS_KEY,
                gson.toJson(recentTracks.toTypedArray())
            )
            .apply()

        return recentTracks
    }

    fun getRecentTracks(): List<Track> {
        val recentTracks = getSharedPreferences(context)
            .getString(SHARED_PREFERENCES_RECENT_TRACKS_KEY, "")

        if (recentTracks.isNullOrEmpty()) {
            return emptyList()
        }

        val result = gson.fromJson(recentTracks, Array<Track>::class.java)

        return result.toList()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        if (preferences == null) {
            preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Application.MODE_PRIVATE)
        }

        return preferences!!
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "PLAYLIST_MAKER_SHARED_PREFERENCES"
        private const val SHARED_PREFERENCES_RECENT_TRACKS_KEY = "SHARED_PREFERENCES_RECENT_TRACKS_KEY"
        private const val MAX_COUNT = 10
    }
}