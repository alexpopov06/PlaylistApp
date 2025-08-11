package com.practicum.playlistapp

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistapp.data.repositoryImpl.TracksRepositoryImpl
import com.practicum.playlistapp.data.network.RetrofitNetworkClient
import com.practicum.playlistapp.data.repositoryImpl.HistoryRepositoryImpl
import com.practicum.playlistapp.domain.api.TracksInteractor
import com.practicum.playlistapp.domain.repository.TracksRepository
import com.practicum.playlistapp.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.domain.impl.TracksInteractorImpl
import com.practicum.playlistapp.domain.media.MediaPlayerInteractor
import com.practicum.playlistapp.domain.repository.HistoryRepository
import com.practicum.playlistapp.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.domain.usecase.GetHistoryUseCase

object Creator {
    fun getTracksRepository(): TracksRepository = TracksRepositoryImpl(RetrofitNetworkClient())

    fun provideTracksInteractor(): TracksInteractor = TracksInteractorImpl(getTracksRepository())

    fun createMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl()
    }
    fun provideAddToHistoryUseCase(context: Context): AddToHistoryUseCase {
        val repository = provideHistoryRepository(context)
        return AddToHistoryUseCase(repository)
    }

    fun provideGetHistoryUseCase(context: Context): GetHistoryUseCase {
        val repository = provideHistoryRepository(context)
        return GetHistoryUseCase(repository)
    }

    fun provideClearHistoryUseCase(context: Context): ClearHistoryUseCase {
        val repository = provideHistoryRepository(context)
        return ClearHistoryUseCase(repository)
    }
    fun provideHistoryRepository(context: Context): HistoryRepository {
        val sharedPrefs = context.getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        return HistoryRepositoryImpl(sharedPrefs, gson)
    }
}


