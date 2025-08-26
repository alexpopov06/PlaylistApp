package com.practicum.playlistapp

import android.app.Application
import com.practicum.playlistapp.ui.search.SearchPresenter

class TracksApplication : Application() {

    var moviesSearchPresenter: SearchPresenter? = null

}