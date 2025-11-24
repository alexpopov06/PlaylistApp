package com.practicum.playlistapp.search.domain.db

import com.practicum.playlistapp.search.data.db.TrackEntity
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    fun getFavoriteTracks():Flow<List<Track>>

    suspend fun add(track: TrackEntity)

    suspend fun remove(track: TrackEntity)
}