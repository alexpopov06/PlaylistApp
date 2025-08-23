package com.practicum.playlistapp.domain.repository

import com.practicum.playlistapp.domain.models.Track
import retrofit2.Call

interface TracksRepository {
    fun searchTrack(expression: String): List<Track>
    fun search(query: String): Call<List<Track>>
}