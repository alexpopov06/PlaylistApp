package com.practicum.playlistapp.player.di

import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistapp.player.data.MediaRepository
import com.practicum.playlistapp.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistapp.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.player.presentation.MediaPlayerViewModel
import com.practicum.playlistapp.search.domain.model.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory { MediaPlayer() }

    single<MediaPlayerInteractor.PlayerController> {
        MediaRepository(mediaPlayerProvider = { get() })
    }

    single<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(
            playerController = get()
        )
    }

    viewModel { (track: Track) ->
        MediaPlayerViewModel(
            mediaPlayerInteractor = get(),
            track = track
        )
    }

    single { Gson() }
}
