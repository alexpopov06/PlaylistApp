package com.practicum.playlistapp.search.domain.repository

import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.search.domain.model.Track
import retrofit2.Call

interface TracksRepository {
    fun searchTrack(expression: String): Resource<List<Track>>
    fun search(query: String): Call<List<Track>>
}