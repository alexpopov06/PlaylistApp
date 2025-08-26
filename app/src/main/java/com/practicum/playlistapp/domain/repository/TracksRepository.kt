package com.practicum.playlistapp.domain.repository

import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.domain.models.Track
import retrofit2.Call

interface TracksRepository {
    fun searchTrack(expression: String): Resource<List<Track>>
    fun search(query: String): Call<List<Track>>
}