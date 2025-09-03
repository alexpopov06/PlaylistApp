
package com.practicum.playlistapp.search.presentation

import SearchState
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import com.practicum.playlistapp.search.domain.model.Track

class TracksViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private var lastFailedSearchQuery: String? = null
    private var latestSearchText: String? = null
    private var isClickable = true
    private val handler = Handler(Looper.getMainLooper())
    private val SEARCH_DEBOUNCE_DELAY = 500L

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TAG = "TracksViewModel"

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = Creator.provideTracksInteractor()
                TracksViewModel(interactor)
            }
        }
    }

    /** Отложенный поиск с дебаунсом и блокировкой одинаковых запросов */
    fun searchDebounce(changedText: String) {
        Log.d(TAG, "searchDebounce: text='$changedText', latest='$latestSearchText'")

        if (changedText.isEmpty()) {
            Log.d(TAG, "searchDebounce: empty text, showing history")
            showSearchHistory()
            return
        }

        if (latestSearchText == changedText) {
            Log.d(TAG, "searchDebounce: same text, skipping")
            return
        }
        latestSearchText = changedText

        handler.removeCallbacksAndMessages(null)

        handler.postDelayed({
            renderState(SearchState.Loading)
        }, 300L)

        val searchRunnable = Runnable {
            Log.d(TAG, "Debounced search executing for: '$changedText'")
            performSearch(changedText)
        }

        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        Log.d(TAG, "searchDebounce: scheduled search in $SEARCH_DEBOUNCE_DELAY ms")
    }

    private fun performSearch(query: String) {
        Log.d(TAG, "performSearch: starting search for '$query'")

        tracksInteractor.searchTrack(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>, errorMessage: String?) {
                Log.d(TAG, "performSearch: result - tracks=${foundTracks.size}, error=$errorMessage")
                handler.post {
                    handler.removeCallbacksAndMessages(null)

                    when {
                        errorMessage != null -> {
                            Log.e(TAG, "Search error: $errorMessage")
                            lastFailedSearchQuery = query
                            renderState(SearchState.Error(showClearButton = true))
                        }
                        foundTracks.isEmpty() -> {
                            Log.d(TAG, "Search empty results")
                            lastFailedSearchQuery = query
                            renderState(SearchState.Empty(showClearButton = true))
                        }
                        else -> {
                            Log.d(TAG, "Search success: ${foundTracks.size} tracks found")
                            lastFailedSearchQuery = null
                            renderState(SearchState.Content(foundTracks, showClearButton = true))
                        }
                    }
                }
            }
        })
    }

    fun clearSearch() {
        Log.d(TAG, "clearSearch")
        handler.removeCallbacksAndMessages(null)
        latestSearchText = ""
        showSearchHistory()
    }


    fun onUpdateClicked() {
        Log.d(TAG, "onUpdateClicked: lastFailedQuery='$lastFailedSearchQuery'")
        handler.removeCallbacksAndMessages(null)
        renderState(SearchState.Loading)
        lastFailedSearchQuery?.let { performSearch(it) }
    }

    fun clearHistory() {
        Log.d(TAG, "clearHistory")
        handler.removeCallbacksAndMessages(null)
        Creator.provideClearHistoryUseCase().execute()
        renderState(SearchState.Idle)
    }

    fun showSearchHistory() {
        Log.d(TAG, "showSearchHistory")
        handler.removeCallbacksAndMessages(null)
        val historyTracks = Creator.provideGetHistoryUseCase().execute()
        Log.d(TAG, "History tracks count: ${historyTracks.size}")
        if (historyTracks.isEmpty()) {
            renderState(SearchState.Idle)
        } else {
            renderState(SearchState.History)
        }
    }


    fun delayDebounce(): Boolean {
        val current = isClickable
        Log.d(TAG, "delayDebounce: current=$current")
        if (isClickable) {
            isClickable = false
            handler.postDelayed({
                isClickable = true
                Log.d(TAG, "delayDebounce: reset to clickable")
            }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    fun onFocusChanged(hasFocus: Boolean, currentText: String) {
        Log.d(TAG, "onFocusChanged: hasFocus=$hasFocus, text='$currentText'")
        handler.removeCallbacksAndMessages(null)
        if (hasFocus && currentText.isEmpty()) showSearchHistory()
    }


    private fun renderState(state: SearchState) {
        Log.d(TAG, "renderState: $state")
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}