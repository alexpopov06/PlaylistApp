package com.practicum.playlistapp.player.data

import android.media.MediaPlayer
import com.practicum.playlistapp.player.domain.api.MediaPlayerInteractor

class MediaRepository : MediaPlayerInteractor.PlayerController {
    private val mediaPlayer = MediaPlayer()
    private var playerState = 0
    private var listener: MediaPlayerInteractor.PlayerListener? = null

    override fun preparePlayer(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            setOnPreparedListener {
                playerState = MediaPlayerInteractor.Companion.STATE_PREPARED
                listener?.onPrepared()
            }
            setOnCompletionListener {
                playerState = MediaPlayerInteractor.Companion.STATE_PREPARED
                listener?.onPlaybackCompleted()
            }
            prepareAsync()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = MediaPlayerInteractor.Companion.STATE_PLAYING
        listener?.startingPlayer()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = MediaPlayerInteractor.Companion.STATE_PAUSED
        listener?.pausingPlayer()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    override fun setListener(listener: MediaPlayerInteractor.PlayerListener) {
        this.listener = listener
    }

    override fun getPlayerState(): Int {
        return playerState
    }
}