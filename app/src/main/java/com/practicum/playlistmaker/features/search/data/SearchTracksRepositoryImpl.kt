package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.data.dto.TracksResponseItem
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.features.search.domain.models.Track
import com.practicum.playlistmaker.features.search.ui.TracksApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchTracksRepositoryImpl(): SearchTracksRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl(SEARCH_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TracksApi::class.java)

    override fun search(text: String): List<Track>? {
        val response = tracksService.searchTracks(text).execute()

        if (response.code() != 200) {
            return null
        }

        val results = response.body()?.results
        if (results?.isNotEmpty() == true) {
            val formattedTracks = results.map {
                formatTrack(it)
            }

           return formattedTracks
        } else {
            return emptyList()
        }
    }

    private fun formatTrack(data: TracksResponseItem): Track {
        val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(data.trackTimeMillis)

        return Track(
            id = data.trackId,
            trackName = data.trackName,
            artistName = data.artistName,
            trackTime = trackTime,
            country = data.country,
            primaryGenreName = data.primaryGenreName,
            year = extractYear(data.releaseDate),
            artworkUrl100 = data.artworkUrl100,
            collectionName = data.collectionName,
            previewUrl = data.previewUrl,
        )
    }

    private fun extractYear(dateTimeString: String): Int {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val dateTime = OffsetDateTime.parse(dateTimeString, formatter)

        return dateTime.year
    }

    companion object {
        private const val SEARCH_URL = "https://itunes.apple.com"
    }
}