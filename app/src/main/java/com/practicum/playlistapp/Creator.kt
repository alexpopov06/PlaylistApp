package com.practicum.playlistapp

import com.practicum.playlistapp.data.TracksRepositoryImpl
import com.practicum.playlistapp.data.network.RetrofitNetworkClient
import com.practicum.playlistapp.domain.api.TracksInteractor
import com.practicum.playlistapp.domain.api.TracksRepository
import com.practicum.playlistapp.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideMoviesInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}