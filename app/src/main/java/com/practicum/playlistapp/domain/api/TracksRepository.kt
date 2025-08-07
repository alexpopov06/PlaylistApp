package com.practicum.playlistapp.domain.api


import com.practicum.playlistapp.domain.models.Track

interface TracksRepository {
    fun searchTrack(expression: String): List<Track>
}