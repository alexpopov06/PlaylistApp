package com.practicum.playlistapp.search.presentation

import SearchState
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistapp.TracksApplication
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.domain.api.TracksInteractor
import com.practicum.playlistapp.domain.models.Track
import com.practicum.playlistapp.ui.search.SearchPresenter

class TracksViewModel(private val context: Context): ViewModel() {
    private var lastFailedSearchQuery: String? = null
    private val debounceDelay = 2000L
    private var searchRunnable: Runnable? = null
    private var isClickable = true
    private val handler = Handler(Looper.getMainLooper())
    private var state: SearchState = SearchState.Idle
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val clearHistoryUseCase = Creator.provideClearHistoryUseCase(context)
    private val getHistoryUseCase = Creator.provideGetHistoryUseCase(context)

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getFactory(value: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as TracksApplication)
                TracksViewModel(app)
            }
        }
    }
    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    fun delayDebounce(): Boolean {
        val current = isClickable
        if (isClickable) {
            isClickable = false
            handler.postDelayed({ isClickable = true },
                SearchPresenter.Companion.CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }
    private fun performSearch(query: String) {
        renderState(SearchState.Loading)
        tracksInteractor.searchTrack(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>, errorMessage: String?) {
                handler.post {
                    when {
                        errorMessage != null -> {
                            renderState(SearchState.Error)
                            lastFailedSearchQuery = query
                        }
                        foundTracks.isEmpty() -> {
                            renderState(SearchState.Empty)
                            lastFailedSearchQuery = query
                        }
                        else -> {
                            renderState(SearchState.Content(foundTracks))
                            lastFailedSearchQuery = null
                        }
                    }
                }
            }
        })
    }
    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

}