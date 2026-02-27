package com.practicum.playlistapp.search.domain.impl

import com.practicum.playlistapp.search.data.db.TrackEntity
import com.practicum.playlistapp.search.domain.db.FavoriteTracksInteractor
import com.practicum.playlistapp.search.domain.db.FavoriteTracksRepository
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getListMedia()
    }

    override suspend fun add(track: TrackEntity) {
        repository.addToMedia(track)
    }

    override suspend fun remove(track: TrackEntity) {
        repository.removeFromMedia(track)
    }
    override suspend fun isFavorite(trackId: String): Boolean {
        return repository.isFavorite(trackId)
    }

}
