package com.practicum.playlistapp.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistapp.player.domain.api.PlayerInteractor
import com.practicum.playlistapp.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

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
    private var timer: Timer? = null

    init {
        mediaPlayerInteractor.setListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        mediaPlayerInteractor.releasePlayer()
    }

    fun onPlayButtonClicked() {
        mediaPlayerInteractor.playbackControl()
    }

     fun preparePlayer() {
        mediaPlayerInteractor.preparePlayer(track.previewUrl)
    }

    private fun startTimer() {
        stopTimer()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                updateProgress()
            }
        }, 0, 200)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun updateProgress() {
        val position = mediaPlayerInteractor.getCurrentPosition()
        progressTimeLiveData.postValue(timeFormat.format(position))
    }

    // Реализация PlayerListener
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