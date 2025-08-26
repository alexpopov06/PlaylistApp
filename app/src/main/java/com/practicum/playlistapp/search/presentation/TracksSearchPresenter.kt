package com.practicum.playlistapp.ui.search

import SearchState
import android.os.Handler
import com.practicum.playlistapp.domain.api.TracksInteractor
import com.practicum.playlistapp.domain.usecase.ClearHistoryUseCase
import com.practicum.playlistapp.domain.usecase.GetHistoryUseCase
import com.practicum.playlistapp.domain.models.Track
import com.practicum.playlistapp.search.presentation.SearchView

class SearchPresenter(
    private val view: SearchView,
    private val tracksInteractor: TracksInteractor,
    private val handler: Handler ,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val getHistoryUseCase: GetHistoryUseCase
) {

    private var lastFailedSearchQuery: String? = null
    private var latestSearchText: String? = null
    private val debounceDelay = 2000L
    private var searchRunnable: Runnable? = null
    private var isClickable = true
    private var state: SearchState = SearchState.Idle

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    fun onTextChanged(query: String) {
        searchRunnable?.let { handler.removeCallbacks(it) }

        if (query.isEmpty()) {
            showSearchHistory()
            view.showClearButton(false)
            return
        }

        view.showClearButton(true)


        if (query == latestSearchText) return
        latestSearchText = query

        searchRunnable = Runnable { performSearch(query) }
        handler.postDelayed(searchRunnable!!, debounceDelay)
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

    fun onClearClicked() {
        view.clearSearchInput()
        showSearchHistory()
        view.showClearButton(false)
    }

    fun onFocusChanged(hasFocus: Boolean, currentText: String) {
        if (hasFocus && currentText.isEmpty()) showSearchHistory()
    }

    fun delayDebounce(): Boolean {
        val current = isClickable
        if (isClickable) {
            isClickable = false
            handler.postDelayed({ isClickable = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun onUpdateClicked() {
        lastFailedSearchQuery?.let { performSearch(it) }
    }

    fun showSearchHistory() {
        val historyTracks = getHistoryUseCase.execute()
        if (historyTracks.isEmpty()) {
            renderState(SearchState.Idle)
        } else {
            renderState(SearchState.History)
        }
    }

    fun clearHistory() {
        clearHistoryUseCase.execute()
        renderState(SearchState.Idle)
    }

    fun clearSearch() {
        view.clearSearchInput()
        showSearchHistory()
        view.showClearButton(false)
    }


    private fun renderState(state: SearchState) {
        this.state = state
        view.render(state)
    }
}
