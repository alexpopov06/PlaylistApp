package com.practicum.playlistapp.player.di

import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistapp.player.data.PlayerRepository
import com.practicum.playlistapp.player.domain.api.PlayerInteractor
import com.practicum.playlistapp.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistapp.player.presentation.PlayerViewModel
import com.practicum.playlistapp.search.domain.model.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {


    factory { MediaPlayer() }


    single<PlayerInteractor.PlayerController> {
        PlayerRepository(mediaPlayerProvider = { get() })
    }


    single<PlayerInteractor> {
        PlayerInteractorImpl(playerController = get())
    }


    viewModel { (track: Track) ->
        PlayerViewModel(
            mediaPlayerInteractor = get(),
            favoriteTracksInteractor = get(),
            track = track
        )
    }


    single { Gson() }
}
