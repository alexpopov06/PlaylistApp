package com.practicum.playlistapp.search.data.repositoryImpl

import com.practicum.playlistapp.search.data.db.AppDatabase
import com.practicum.playlistapp.search.data.db.TrackEntity
import com.practicum.playlistapp.search.domain.db.FavoriteTracksRepository
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(private val db: AppDatabase) : FavoriteTracksRepository {

    private val dao = db.trackDao()

    override suspend fun addToMedia(track: TrackEntity) {
        android.util.Log.d("FAV_DEBUG", "Repository.addToMedia: id=${track.id}, name=${track.trackName}")
        dao.insertTrack(track)
    }

    override suspend fun removeFromMedia(track: TrackEntity) {
        android.util.Log.d("FAV_DEBUG", "Repository.removeFromMedia: id=${track.id}, name=${track.trackName}")
        dao.deleteTrack(track)
    }

    override fun getListMedia(): Flow<List<Track>> {
        return dao.getListOfTracks()
            .map { list ->
                android.util.Log.d("FAV_DEBUG", "Repository.getListMedia: from DB size=${list.size}, ids=${list.joinToString { it.id }}")

                list
                    .sortedByDescending { it.addedAt }
                    .map { it.toDomain() }
                    .also { mapped ->
                        android.util.Log.d("FAV_DEBUG", "Repository.getListMedia: after map size=${mapped.size}, names=${mapped.joinToString { it.trackName ?: "null" }}")
                    }
            }
    }


}


private fun TrackEntity.toDomain(): Track {
    return Track(
        trackId = this.id,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl,
        isFavorite = true
    )
}
