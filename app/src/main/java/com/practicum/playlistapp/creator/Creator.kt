package com.practicum.playlistapp.creator

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistapp.search.data.network.RetrofitNetworkClient
import com.practicum.playlistapp.search.data.repositoryImpl.HistoryRepositoryImpl
import com.practicum.playlistapp.search.data.repositoryImpl.TracksRepositoryImpl
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import com.practicum.playlistapp.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistapp.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistapp.search.domain.repository.HistoryRepository
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import com.practicum.playlistapp.search.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.GetHistoryUseCase
import com.practicum.playlistapp.sharing.domain.api.SharingInteractor
import com.practicum.playlistapp.sharing.domain.impl.SharingInteractorImpl

object Creator {

    private lateinit var historyRepository: HistoryRepository
    private lateinit var addToHistoryUseCase: AddToHistoryUseCase
    private lateinit var getHistoryUseCase: GetHistoryUseCase
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase
    private lateinit var sharingInteractor: SharingInteractor

    fun initialize(context: Context) {

        val sharedPrefs = context.getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        historyRepository = HistoryRepositoryImpl(sharedPrefs, gson)


        addToHistoryUseCase = AddToHistoryUseCase(historyRepository)
        getHistoryUseCase = GetHistoryUseCase(historyRepository)
        clearHistoryUseCase = ClearHistoryUseCase(historyRepository)

        sharingInteractor = SharingInteractorImpl(context)
    }

    fun getTracksRepository(): TracksRepository = TracksRepositoryImpl(RetrofitNetworkClient())

    fun provideTracksInteractor(): TracksInteractor = TracksInteractorImpl(getTracksRepository())

    fun createMediaPlayerInteractor(): MediaPlayerInteractor = MediaPlayerInteractorImpl()

    fun provideAddToHistoryUseCase(): AddToHistoryUseCase = addToHistoryUseCase

    fun provideGetHistoryUseCase(): GetHistoryUseCase = getHistoryUseCase

    fun provideClearHistoryUseCase(): ClearHistoryUseCase = clearHistoryUseCase

    fun provideSharingInteractor(): SharingInteractor = sharingInteractor
}