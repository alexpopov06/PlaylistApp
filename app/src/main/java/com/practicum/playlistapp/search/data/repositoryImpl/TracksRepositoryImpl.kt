package com.practicum.playlistapp.search.data.repositoryImpl

import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.search.data.NetworkClient
import com.practicum.playlistapp.search.data.dto.SearchTracksRequest
import com.practicum.playlistapp.search.data.dto.SearchTracksResponse
import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {

        try {
            val response = networkClient.doRequest(SearchTracksRequest(expression))

            when (response.resultCode) {

                -1 -> emit(Resource.Error("Проверьте подключение к интернету"))

                200 -> {
                    val tracks = (response as SearchTracksResponse).results.map { dto ->
                        Track(
                            trackName = dto.trackName,
                            artistName = dto.artistName,
                            trackTimeMillis = dto.trackTimeMillis,
                            artworkUrl100 = dto.artworkUrl100,
                            trackId = dto.trackId?.toString(),
                            collectionName = dto.collectionName,
                            releaseDate = dto.releaseDate,
                            primaryGenreName = dto.primaryGenreName,
                            country = dto.country,
                            previewUrl = dto.previewUrl
                        )
                    }

                    emit(Resource.Success(tracks))
                }

                else -> emit(Resource.Error("Ошибка сервера"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("NETWORK_ERROR: ${e.message}"))
        }
    }
}
