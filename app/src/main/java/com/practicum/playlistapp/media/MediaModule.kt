package com.practicum.playlistapp.media

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { FirstFragmentViewModel() }
    viewModel { SecondFragmentViewModel(get()) }
}