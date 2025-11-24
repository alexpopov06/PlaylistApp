package com.practicum.playlistapp.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistapp.search.domain.db.FavoriteTracksInteractor
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeState(): LiveData<FavoriteTracksState> = stateLiveData

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { list: List<Track> ->
                android.util.Log.d("FAV_DEBUG", "ViewModel.loadFavorites: collected size=${list.size}")

                if (list.isEmpty()) {
                    android.util.Log.d("FAV_DEBUG", "ViewModel.loadFavorites: state = Empty")
                    stateLiveData.postValue(FavoriteTracksState.Empty)
                } else {
                    android.util.Log.d("FAV_DEBUG", "ViewModel.loadFavorites: state = Content, first=${list.first().trackName}")
                    stateLiveData.postValue(FavoriteTracksState.Content(list))
                }
            }
        }
    }

}
