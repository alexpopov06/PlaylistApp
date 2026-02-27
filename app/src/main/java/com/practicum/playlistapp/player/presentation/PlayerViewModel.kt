package com.practicum.playlistapp.player.presentation

import androidx.lifecycle.*
import com.practicum.playlistapp.player.domain.api.PlayerInteractor
import com.practicum.playlistapp.search.domain.db.FavoriteTracksInteractor
import com.practicum.playlistapp.search.data.db.TrackEntity
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val track: Track
) : ViewModel(), PlayerInteractor.PlayerListener {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    private val trackLiveData = MutableLiveData(track)
    fun observeTrack(): LiveData<Track> = trackLiveData

    private val isFavoriteLiveData = MutableLiveData(track.isFavorite)
    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    private var timerJob: Job? = null

    init {
        mediaPlayerInteractor.setListener(this)

        viewModelScope.launch {
            val isFav = favoriteTracksInteractor.isFavorite(track.trackId ?: "")
            track.isFavorite = isFav
            isFavoriteLiveData.postValue(isFav)
            trackLiveData.postValue(track)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        mediaPlayerInteractor.releasePlayer()
    }

    fun preparePlayer() {
        mediaPlayerInteractor.preparePlayer(track.previewUrl)
    }

    fun onPlayButtonClicked() {
        mediaPlayerInteractor.playbackControl()
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (playerStateLiveData.value == STATE_PLAYING) {
                progressTimeLiveData.postValue(timeFormat.format(mediaPlayerInteractor.getCurrentPosition()))
                delay(300)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onPrepared() {
        playerStateLiveData.postValue(STATE_PREPARED)
    }

    override fun onPlaybackCompleted() {
        playerStateLiveData.postValue(STATE_PREPARED)
        stopTimer()
        progressTimeLiveData.postValue("00:00")
    }

    override fun startingPlayer() {
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimer()
    }

    override fun pausingPlayer() {
        playerStateLiveData.postValue(STATE_PAUSED)
        stopTimer()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTracksInteractor.remove(track.toEntity())
                track.isFavorite = false
            } else {
                favoriteTracksInteractor.add(track.toEntity())
                track.isFavorite = true
            }

            isFavoriteLiveData.postValue(track.isFavorite)
            trackLiveData.postValue(track)
        }
    }

    private fun Track.toEntity(): TrackEntity {
        return TrackEntity(
            id = this.trackId ?: "",
            trackName = this.trackName,
            artistName = this.artistName,
            trackTimeMillis = this.trackTimeMillis,
            artworkUrl100 = this.artworkUrl100,
            trackId = this.trackId,
            collectionName = this.collectionName,
            releaseDate = this.releaseDate,
            primaryGenreName = this.primaryGenreName,
            country = this.country,
            previewUrl = this.previewUrl,
            addedAt = System.currentTimeMillis()
        )
    }
}
