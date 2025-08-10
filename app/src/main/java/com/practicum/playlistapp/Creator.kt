package com.practicum.playlistapp

import com.practicum.playlistapp.data.repositoryImpl.TracksRepositoryImpl
import com.practicum.playlistapp.data.network.RetrofitNetworkClient
import com.practicum.playlistapp.domain.api.TracksInteractor
import com.practicum.playlistapp.domain.repository.TracksRepository
import com.practicum.playlistapp.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.domain.impl.TracksInteractorImpl
import com.practicum.playlistapp.domain.media.MediaPlayerInteractor

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideMoviesInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
    fun createMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl()
    }
}