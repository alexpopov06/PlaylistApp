package com.practicum.playlistapp.search.presentation

import SearchState
import android.util.Log
import androidx.lifecycle.*
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.domain.usecase.AddToHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.search.domain.usecase.GetHistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TracksViewModel(
    private val tracksInteractor: TracksInteractor,
    private val addToHistoryUseCase: AddToHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TAG = "TracksViewModel"
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null
    private var lastFailedSearchQuery: String? = null

    private var searchJob: Job? = null
    private var clickJob: Job? = null
    private var isClickable = true

    fun searchDebounce(text: String) {
        Log.d(TAG, "searchDebounce: '$text'")

        if (text.isEmpty()) {
            showSearchHistory()
            return
        }

        if (latestSearchText == text) return
        latestSearchText = text

        searchJob?.cancel()

        stateLiveData.postValue(SearchState.Loading)

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            runSearch(text)
        }
    }


    fun clickDebounce(): Boolean {
        if (!isClickable) return false

        isClickable = false

        clickJob?.cancel()
        clickJob = viewModelScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            isClickable = true
        }
        return true
    }


    private fun runSearch(query: String) {
        Log.d(TAG, "runSearch(): '$query'")

        viewModelScope.launch {
            tracksInteractor.searchTrack(query).collectLatest { (tracks, error) ->

                when {
                    error?.contains("NETWORK_ERROR") == true -> {
                        lastFailedSearchQuery = query
                        renderState(SearchState.NoWifi)
                    }

                    error != null -> {
                        lastFailedSearchQuery = query
                        renderState(SearchState.Error(showClearButton = true))
                    }

                    tracks.isNullOrEmpty() -> {
                        lastFailedSearchQuery = query
                        renderState(SearchState.Empty(showClearButton = true))
                    }

                    else -> {
                        lastFailedSearchQuery = null
                        addToHistoryUseCase.execute(tracks.first())
                        renderState(SearchState.Content(tracks, showClearButton = true))
                    }
                }
            }
        }
    }



    fun clearSearch() {
        searchJob?.cancel()
        latestSearchText = ""
        showSearchHistory()
    }

    fun onUpdateClicked() {
        lastFailedSearchQuery?.let { runSearch(it) }
    }

    fun clearHistory() {
        clearHistoryUseCase.execute()
        renderState(SearchState.Idle)
    }

    fun showSearchHistory() {
        val history = getHistoryUseCase.execute()
        if (history.isEmpty())
            renderState(SearchState.Idle)
        else
            renderState(SearchState.History(history))
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }
}
