package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.features.media.data.db.AppDatabase
import com.practicum.playlistmaker.features.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.features.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.features.search.data.RecentTracksDiskDataSource
import com.practicum.playlistmaker.features.search.data.RecentTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.data.SearchTracksRemoteDataSource
import com.practicum.playlistmaker.features.search.data.SearchTracksRemoteDataSource.TracksApi
import com.practicum.playlistmaker.features.search.data.SearchTracksRepositoryImpl
import com.practicum.playlistmaker.features.search.data.formatters.TrackFormatter
import com.practicum.playlistmaker.features.search.domain.api.RecentTracksRepository
import com.practicum.playlistmaker.features.search.domain.api.SearchTracksRepository
import com.practicum.playlistmaker.features.settings.data.SettingsDiskDataSource
import com.practicum.playlistmaker.features.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.features.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

val dataModule = module {
    // Search

    single<TracksApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TracksApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("PLAYLIST_MAKER_SHARED_PREFERENCES", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single {
        TrackFormatter()
    }

    // Вопрос
    factory {
        Executors.newCachedThreadPool()
    }

    single {
        RecentTracksDiskDataSource(get(), get())
    }

    single<RecentTracksRepository> {
        RecentTracksRepositoryImpl(get(), get(), get())
    }

    single {
        SearchTracksRemoteDataSource(get())
    }

    single<SearchTracksRepository> {
        SearchTracksRepositoryImpl(get(), get())
    }

    // Player

    // Вопрос
    factory {
        MediaPlayer()
    }

    // Вопрос
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    // Settings

    single {
        SettingsDiskDataSource(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    // Media

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

}
