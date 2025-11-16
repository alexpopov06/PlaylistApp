package com.practicum.playlistapp.search.domain.impl

import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTrack(expression).map { result ->

            when (result) {
                is Resource.Success -> Pair(result.data, null)
                is Resource.Error -> Pair(null, result.message)
                else -> Pair(null, null)
            }
        }
    }
}
