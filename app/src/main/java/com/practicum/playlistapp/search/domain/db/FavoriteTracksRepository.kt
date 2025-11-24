package com.practicum.playlistapp.search.domain.db

import com.practicum.playlistapp.search.data.db.TrackEntity
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addToMedia(track: TrackEntity)

    suspend fun removeFromMedia(track: TrackEntity)

     fun getListMedia(): Flow<List<Track>>
}