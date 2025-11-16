package com.practicum.playlistapp.search.domain.api

import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {



    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>
}
