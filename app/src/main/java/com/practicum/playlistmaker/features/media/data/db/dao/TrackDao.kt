package com.practicum.playlistmaker.features.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.features.media.data.db.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Delete
    fun removeTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table")
    fun getAllTracks(): List<TrackEntity>

    @Query("SELECT id FROM track_table")
    fun getAllTrackIds(): List<Int>
}
