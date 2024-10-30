package com.practicum.playlistmaker.features.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.features.search.data.dto.TrackDto

class RecentTracksDiskDataSource(
    private val preferences: SharedPreferences,
    private val gson: Gson,
) {
    fun clear() {
        preferences
            .edit()
            .remove(SHARED_PREFERENCES_RECENT_TRACKS_KEY,)
            .apply()
    }

    fun add(trackDto: TrackDto): List<TrackDto> {
        val recentTracks = getRecentTracks()
            .filter { it.id != trackDto.id }
            .toMutableList()

        recentTracks.add(0, trackDto)

        if (recentTracks.size > MAX_COUNT) {
            recentTracks.removeLast()
        }

        preferences
            .edit()
            .putString(
                SHARED_PREFERENCES_RECENT_TRACKS_KEY,
                gson.toJson(recentTracks.toTypedArray())
            )
            .apply()

        return recentTracks
    }

    fun getRecentTracks(): List<TrackDto> {
        val recentTracks = preferences
            .getString(SHARED_PREFERENCES_RECENT_TRACKS_KEY, "")

        if (recentTracks.isNullOrEmpty()) {
            return emptyList()
        }

        val parseResult = runCatching {
            gson.fromJson(recentTracks, Array<TrackDto>::class.java).toList()
        }

        if (parseResult.isFailure) {
            return emptyList()
        }

        return parseResult.getOrThrow()
    }

    companion object {
        private const val SHARED_PREFERENCES_RECENT_TRACKS_KEY = "SHARED_PREFERENCES_RECENT_TRACKS_KEY"
        private const val MAX_COUNT = 10
    }
}