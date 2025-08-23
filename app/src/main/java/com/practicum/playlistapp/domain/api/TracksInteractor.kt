package com.practicum.playlistapp.domain.api

import com.practicum.playlistapp.domain.models.Track

interface TracksInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}
