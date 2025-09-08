package com.practicum.playlistapp.player.domain.impl

import com.practicum.playlistapp.player.domain.api.MediaPlayerInteractor

class MediaPlayerInteractorImpl(
    private val playerController: MediaPlayerInteractor.PlayerController
) : MediaPlayerInteractor {

    override fun preparePlayer(url: String) {
        playerController.preparePlayer(url)
    }

    override fun startPlayer() {
        playerController.startPlayer()
    }

    override fun pausePlayer() {
        playerController.pausePlayer()
    }

    override fun playbackControl() {
        when(playerController.getPlayerState()) {
            MediaPlayerInteractor.STATE_PLAYING -> {
                pausePlayer()
            }
            MediaPlayerInteractor.STATE_PREPARED, MediaPlayerInteractor.STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun releasePlayer() {
        playerController.releasePlayer()
    }

    override fun getCurrentPosition(): Long {
        return playerController.getCurrentPosition()
    }

    override fun setListener(listener: MediaPlayerInteractor.PlayerListener) {
        playerController.setListener(listener)
    }
}