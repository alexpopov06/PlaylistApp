package com.practicum.playlistapp.sharing.di

import com.practicum.playlistapp.sharing.domain.api.SharingInteractor
import com.practicum.playlistapp.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module {
    single<SharingInteractor> {
        SharingInteractorImpl(androidContext())

    }
}