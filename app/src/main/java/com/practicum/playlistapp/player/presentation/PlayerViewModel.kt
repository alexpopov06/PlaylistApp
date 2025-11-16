package com.practicum.playlistapp.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistapp.player.domain.api.PlayerInteractor
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayerInteractor: PlayerInteractor,
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

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())


    private var timerJob: Job? = null

    init {
        mediaPlayerInteractor.setListener(this)
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
            while (true) {
                val pos = mediaPlayerInteractor.getCurrentPosition()

                progressTimeLiveData.postValue(timeFormat.format(pos))

                delay(300L)

                if (playerStateLiveData.value != STATE_PLAYING) break
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
}
