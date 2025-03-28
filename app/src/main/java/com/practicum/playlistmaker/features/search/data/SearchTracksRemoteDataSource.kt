package com.practicum.playlistmaker.features.search.data

import com.practicum.playlistmaker.features.search.data.dto.TracksResponse
import com.practicum.playlistmaker.features.search.data.dto.TracksResponseItem
import com.practicum.playlistmaker.features.search.domain.models.Track
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Locale

class SearchTracksRemoteDataSource(
    private val tracksService: TracksApi,
) {
    suspend fun getSearchResults(text: String): List<Track>? {
        val response = try {
            tracksService.searchTracks(text)
        } catch(e: Exception) {
            return null
        }

        val results = response.results
        if (results.isNotEmpty()) {
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
            year = data.releaseDate?.let {
                extractYear(it)
            },
            artworkUrl100 = data.artworkUrl100,
            collectionName = data.collectionName,
            previewUrl = data.previewUrl,
        )
    }

    private fun extractYear(dateTimeString: String): Int {
        return dateTimeString.split("-", limit = 2)[0].toInt()
    }

    interface TracksApi {
        @GET("/search?entity=song")
        suspend fun searchTracks(@Query("term") text: String): TracksResponse
    }
}