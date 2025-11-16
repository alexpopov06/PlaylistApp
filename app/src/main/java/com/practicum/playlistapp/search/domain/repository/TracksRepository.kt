package com.practicum.playlistapp.search.domain.repository

import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    fun searchTrack(expression: String): Flow<Resource<List<Track>>>
}
