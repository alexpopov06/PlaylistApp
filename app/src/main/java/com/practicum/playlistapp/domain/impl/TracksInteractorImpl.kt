package com.practicum.playlistapp.domain.impl

import com.practicum.playlistapp.domain.api.TracksInteractor
import com.practicum.playlistapp.domain.repository.TracksRepository

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    override fun searchTrack(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        val t = Thread {
            consumer.consume(repository.searchTrack(expression))
        }
        t.start()
    }

}