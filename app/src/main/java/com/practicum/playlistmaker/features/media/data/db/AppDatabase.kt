package com.practicum.playlistmaker.features.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.features.media.data.db.dao.TrackDao

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
}
