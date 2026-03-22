package com.practicum.playlistapp.playlist.di

import com.practicum.playlistapp.playlist.data.PlaylistsRepositoryImpl
import com.practicum.playlistapp.playlist.domain.api.PlaylistsInteractor
import com.practicum.playlistapp.playlist.domain.impl.PlaylistsInteractorImpl
import com.practicum.playlistapp.playlist.domain.repository.PlaylistsRepository
import com.practicum.playlistapp.playlist.presentation.CreatePlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {
    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(
            context = androidContext(),
            db = get(),
            gson = get()
        )
    }
    single<PlaylistsInteractor> { PlaylistsInteractorImpl(get()) }
    viewModel { CreatePlaylistViewModel(get()) }
}

