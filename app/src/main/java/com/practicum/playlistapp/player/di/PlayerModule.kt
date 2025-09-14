package com.practicum.playlistapp.player.di

import com.practicum.playlistapp.player.data.MediaRepository
import com.practicum.playlistapp.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistapp.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistapp.player.presentation.MediaPlayerViewModel
import com.practicum.playlistapp.search.domain.model.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module{
    single<MediaPlayerInteractor.PlayerController> { MediaRepository() }
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

}