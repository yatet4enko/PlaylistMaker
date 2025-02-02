package com.practicum.playlistmaker.features.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.features.media.data.db.PlaylistEntity
import com.practicum.playlistmaker.features.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(track: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getAll(): Flow<List<PlaylistEntity>>

    @Query("UPDATE playlist_table SET tracksIds = :updatedTracksIds, tracksCount = tracksCount + 1 WHERE id = :playlistId")
    suspend fun updatePlaylistTracks(playlistId: Int, updatedTracksIds: String)
}
