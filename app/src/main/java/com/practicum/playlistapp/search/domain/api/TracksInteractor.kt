package com.practicum.playlistapp.search.domain.api

import com.practicum.playlistapp.search.domain.model.Track

interface TracksInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, errorMessage: String?)
    }
}