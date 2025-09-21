package com.practicum.playlistapp.player.data

import android.media.MediaPlayer
import com.practicum.playlistapp.player.domain.api.MediaPlayerInteractor

class MediaRepository(
    private val mediaPlayerProvider: () -> MediaPlayer
) : MediaPlayerInteractor.PlayerController {

    private var mediaPlayer: MediaPlayer = mediaPlayerProvider()
    private var playerState = MediaPlayerInteractor.STATE_DEFAULT
    private var listener: MediaPlayerInteractor.PlayerListener? = null
    private var isReleased = false

    override fun preparePlayer(url: String) {
        if (isReleased) {
            mediaPlayer = mediaPlayerProvider()
            isReleased = false
        } else {
            mediaPlayer.reset()
        }

        mediaPlayer.setOnPreparedListener {
            playerState = MediaPlayerInteractor.STATE_PREPARED
            listener?.onPrepared()
        }

        mediaPlayer.setOnCompletionListener {
            playerState = MediaPlayerInteractor.STATE_PREPARED
            listener?.onPlaybackCompleted()
        }

        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
    }

    override fun startPlayer() {
        if (!isReleased) {
            mediaPlayer.start()
            playerState = MediaPlayerInteractor.STATE_PLAYING
            listener?.startingPlayer()
        }
    }

    override fun pausePlayer() {
        if (!isReleased && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playerState = MediaPlayerInteractor.STATE_PAUSED
            listener?.pausingPlayer()
        }
    }

    override fun releasePlayer() {
        if (!isReleased) {
            mediaPlayer.release()
            isReleased = true
            listener = null
            playerState = MediaPlayerInteractor.STATE_DEFAULT
        }
    }

    override fun getCurrentPosition(): Long {
        return if (!isReleased) mediaPlayer.currentPosition.toLong() else 0L
    }

    override fun setListener(listener: MediaPlayerInteractor.PlayerListener) {
        this.listener = listener
    }

    override fun getPlayerState(): Int {
        return playerState
    }
}
