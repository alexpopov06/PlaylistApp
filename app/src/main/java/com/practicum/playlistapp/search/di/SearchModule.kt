// SearchModule.kt
package com.practicum.playlistapp.search.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistapp.search.data.NetworkClient
import com.practicum.playlistapp.search.data.network.RetrofitNetworkClient
import com.practicum.playlistapp.search.data.repositoryImpl.HistoryRepositoryImpl
import com.practicum.playlistapp.search.data.repositoryImpl.TracksRepositoryImpl
import com.practicum.playlistapp.search.data.sharedPrefs.ThemePreferences
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import com.practicum.playlistapp.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistapp.search.domain.repository.HistoryRepository
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import com.practicum.playlistapp.search.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.GetHistoryUseCase
import com.practicum.playlistapp.search.presentation.TracksViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    single<NetworkClient> { RetrofitNetworkClient() }


    single<SharedPreferences> {
        androidContext().getSharedPreferences("SearchHistoryPrefs", Context.MODE_PRIVATE)
    }


    single { Gson() }


    single<TracksRepository> { TracksRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get(), get()) }


    factory { AddToHistoryUseCase(get()) }
    factory { GetHistoryUseCase(get()) }
    factory { ClearHistoryUseCase(get()) }


    single<TracksInteractor> { TracksInteractorImpl(get()) }


    viewModel {
        TracksViewModel(
            tracksInteractor = get(),
            addToHistoryUseCase = get(),
            getHistoryUseCase = get(),
            clearHistoryUseCase = get()
        )
    }


    single { ThemePreferences(androidContext()) }
}