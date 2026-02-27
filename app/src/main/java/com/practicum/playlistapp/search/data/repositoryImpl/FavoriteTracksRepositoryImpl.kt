package com.practicum.playlistapp.search.data.repositoryImpl

import com.practicum.playlistapp.search.data.db.AppDatabase
import com.practicum.playlistapp.search.data.db.TrackEntity
import com.practicum.playlistapp.search.domain.db.FavoriteTracksRepository
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val db: AppDatabase
) : FavoriteTracksRepository {

    private val dao = db.trackDao()

    override suspend fun addToMedia(track: TrackEntity) {
        dao.insertTrack(track)
    }

    override suspend fun removeFromMedia(track: TrackEntity) {
        dao.deleteTrack(track)
    }

    override fun getListMedia(): Flow<List<Track>> {
        return dao.getListOfTracks()
            .map { list ->
                list
                    .sortedByDescending { it.addedAt }
                    .map { it.toDomain() }
            }
    }

    override suspend fun isFavorite(trackId: String): Boolean {
        val ids = dao.getTracksId()
        return ids.contains(trackId)
    }
}

private fun TrackEntity.toDomain(): Track = Track(
    trackId = this.id,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTimeMillis = this.trackTimeMillis,
    artworkUrl100 = this.artworkUrl100.toString(),
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    primaryGenreName = this.primaryGenreName,
    country = this.country,
    previewUrl = this.previewUrl.toString(),
    isFavorite = true
)
