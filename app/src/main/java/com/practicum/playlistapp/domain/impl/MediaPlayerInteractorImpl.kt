package com.practicum.playlistapp.domain.impl

import android.media.MediaPlayer

import com.practicum.playlistapp.domain.media.MediaPlayerInteractor
import com.practicum.playlistapp.domain.media.MediaPlayerInteractor.PlayerListener


class MediaPlayerInteractorImpl: MediaPlayerInteractor {

    private val mediaPlayer = MediaPlayer()
    private var playerState = 0
    private var listener: PlayerListener? = null

    override fun preparePlayer(url: String) {

        mediaPlayer.apply {
            setDataSource(url)

            setOnPreparedListener {


                playerState = MediaPlayerInteractor.STATE_PREPARED
                listener?.onPrepared()
            }
            setOnCompletionListener {
                playerState = MediaPlayerInteractor.STATE_PREPARED

                listener?.onPlaybackCompleted()
            }
            prepareAsync()
        }

    }

    override fun startPlayer() {

        mediaPlayer.start()

        playerState = MediaPlayerInteractor.STATE_PLAYING
        listener?.startingPlayer()
    }

    override fun pausePlayer() {

        mediaPlayer.pause()
        playerState = MediaPlayerInteractor.STATE_PAUSED
        listener?.pausingPlayer()

    }

    override fun playbackControl() {
        when(playerState) {
            MediaPlayerInteractor.STATE_PLAYING -> {
                pausePlayer()
            }
            MediaPlayerInteractor.STATE_PREPARED, MediaPlayerInteractor.STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    override fun setListener(listener: PlayerListener) {
        this.listener = listener
    }


}