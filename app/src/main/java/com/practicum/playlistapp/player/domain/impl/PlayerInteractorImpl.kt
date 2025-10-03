package com.practicum.playlistapp.player.domain.impl

import com.practicum.playlistapp.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val playerController: PlayerInteractor.PlayerController
) : PlayerInteractor {

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
            PlayerInteractor.STATE_PLAYING -> {
                pausePlayer()
            }
            PlayerInteractor.STATE_PREPARED, PlayerInteractor.STATE_PAUSED -> {
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

    override fun setListener(listener: PlayerInteractor.PlayerListener) {
        playerController.setListener(listener)
    }
}