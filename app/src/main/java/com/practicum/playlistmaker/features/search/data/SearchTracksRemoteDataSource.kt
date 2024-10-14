package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.data.dto.TracksResponse
import com.practicum.playlistmaker.features.search.data.dto.TracksResponseItem
import com.practicum.playlistmaker.features.search.domain.models.Track
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchTracksRemoteDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl(SEARCH_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TracksApi::class.java)

    fun getSearchResults(text: String): List<Track>? {
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

    interface TracksApi {
        @GET("/search?entity=song")
        fun searchTracks(@Query("term") text: String): Call<TracksResponse>
    }
}