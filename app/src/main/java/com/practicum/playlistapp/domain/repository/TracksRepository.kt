package com.practicum.playlistapp.domain.repository

import com.practicum.playlistapp.domain.models.Track

interface TracksRepository {
    fun searchTrack(expression: String): List<Track>
}