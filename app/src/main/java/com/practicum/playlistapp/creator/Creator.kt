package com.practicum.playlistapp.creator

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.practicum.playlistapp.data.network.RetrofitNetworkClient
import com.practicum.playlistapp.data.repositoryImpl.HistoryRepositoryImpl
import com.practicum.playlistapp.data.repositoryImpl.TracksRepositoryImpl
import com.practicum.playlistapp.domain.api.TracksInteractor
import com.practicum.playlistapp.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.domain.impl.TracksInteractorImpl
import com.practicum.playlistapp.domain.media.MediaPlayerInteractor
import com.practicum.playlistapp.domain.repository.HistoryRepository
import com.practicum.playlistapp.domain.repository.TracksRepository
import com.practicum.playlistapp.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.domain.usecase.GetHistoryUseCase
import com.practicum.playlistapp.search.presentation.SearchView
import com.practicum.playlistapp.ui.search.SearchPresenter

object Creator {

    fun getTracksRepository(): TracksRepository = TracksRepositoryImpl(RetrofitNetworkClient())

    fun provideTracksInteractor(): TracksInteractor = TracksInteractorImpl(getTracksRepository())

    fun createMediaPlayerInteractor(): MediaPlayerInteractor = MediaPlayerInteractorImpl()

    fun provideHistoryRepository(context: Context): HistoryRepository {
        val sharedPrefs = context.getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        return HistoryRepositoryImpl(sharedPrefs, gson)
    }

    fun provideAddToHistoryUseCase(context: Context): AddToHistoryUseCase =
        AddToHistoryUseCase(provideHistoryRepository(context))

    fun provideGetHistoryUseCase(context: Context): GetHistoryUseCase =
        GetHistoryUseCase(provideHistoryRepository(context))

    fun provideClearHistoryUseCase(context: Context): ClearHistoryUseCase =
        ClearHistoryUseCase(provideHistoryRepository(context))

    fun provideSearchPresenter(view: SearchView, context: Context): SearchPresenter {
        return SearchPresenter(
            view = view,
            tracksInteractor = provideTracksInteractor(),
            handler = Handler(Looper.getMainLooper()),
            clearHistoryUseCase = provideClearHistoryUseCase(context),
            getHistoryUseCase = provideGetHistoryUseCase(context)
        )
    }
}
