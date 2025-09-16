package com.practicum.playlistapp.player.domain.api

interface MediaPlayerInteractor {
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun releasePlayer()
    fun getCurrentPosition(): Long
    fun setListener(listener: PlayerListener)

    interface PlayerListener {
        fun onPrepared()
        fun onPlaybackCompleted()
        fun startingPlayer()
        fun pausingPlayer()
    }

    interface PlayerController {
        fun preparePlayer(url: String)
        fun startPlayer()
        fun pausePlayer()
        fun releasePlayer()
        fun getCurrentPosition(): Long
        fun setListener(listener: PlayerListener)
        fun getPlayerState(): Int
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
}