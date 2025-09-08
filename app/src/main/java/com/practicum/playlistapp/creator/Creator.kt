package com.practicum.playlistapp.creator

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistapp.player.data.MediaRepository
import com.practicum.playlistapp.search.data.network.RetrofitNetworkClient
import com.practicum.playlistapp.search.data.repositoryImpl.HistoryRepositoryImpl
import com.practicum.playlistapp.search.data.repositoryImpl.TracksRepositoryImpl
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import com.practicum.playlistapp.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistapp.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistapp.search.data.sharedPrefs.ThemePreferences
import com.practicum.playlistapp.search.domain.repository.HistoryRepository
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import com.practicum.playlistapp.search.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.GetHistoryUseCase
import com.practicum.playlistapp.sharing.domain.api.SharingInteractor
import com.practicum.playlistapp.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistapp.settings.data.repositoryImpl.ThemeRepositoryImpl

import com.practicum.playlistapp.settings.domain.repository.ThemeRepository

object Creator {

    private lateinit var context: Context
    private lateinit var historyRepository: HistoryRepository
    private lateinit var themeRepository: ThemeRepository
    private lateinit var addToHistoryUseCase: AddToHistoryUseCase
    private lateinit var getHistoryUseCase: GetHistoryUseCase
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase
    private lateinit var sharingInteractor: SharingInteractor

    fun initialize(appContext: Context) {
        context = appContext.applicationContext

        val sharedPrefs = context.getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
        val gson = Gson()


        historyRepository = HistoryRepositoryImpl(sharedPrefs, gson)
        themeRepository = ThemeRepositoryImpl(
            ThemePreferences(context),
            context
        )


        addToHistoryUseCase = AddToHistoryUseCase(historyRepository)
        getHistoryUseCase = GetHistoryUseCase(historyRepository)
        clearHistoryUseCase = ClearHistoryUseCase(historyRepository)

        sharingInteractor = SharingInteractorImpl(context)
    }

    fun getTracksRepository(): TracksRepository = TracksRepositoryImpl(RetrofitNetworkClient())

    fun provideTracksInteractor(): TracksInteractor = TracksInteractorImpl(getTracksRepository())


    fun provideAddToHistoryUseCase(): AddToHistoryUseCase = addToHistoryUseCase

    fun provideGetHistoryUseCase(): GetHistoryUseCase = getHistoryUseCase

    fun provideClearHistoryUseCase(): ClearHistoryUseCase = clearHistoryUseCase

    fun provideSharingInteractor(): SharingInteractor = sharingInteractor

    fun provideThemeRepository(): ThemeRepository = themeRepository
    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        val playerController = MediaRepository()
        return MediaPlayerInteractorImpl(playerController)
    }
}