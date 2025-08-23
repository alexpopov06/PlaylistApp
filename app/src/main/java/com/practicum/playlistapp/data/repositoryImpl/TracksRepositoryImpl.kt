package com.practicum.playlistapp.data.repositoryImpl

import com.practicum.playlistapp.data.NetworkClient
import com.practicum.playlistapp.data.dto.SearchTracksRequest
import com.practicum.playlistapp.data.dto.SearchTracksResponse
import com.practicum.playlistapp.domain.models.Track
import com.practicum.playlistapp.domain.repository.TracksRepository
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchTracksRequest(expression))
        return if (response.resultCode == 200) {
            (response as SearchTracksResponse).results.map { dto ->
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
        } else {
            emptyList()
        }
    }

    override fun search(query: String): Call<List<Track>> {
        return object : Call<List<Track>> {
            override fun execute(): Response<List<Track>> {
                val response = networkClient.doRequest(SearchTracksRequest(query))
                return if (response.resultCode == 200) {
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
                    Response.success(tracks)
                } else {
                    Response.error(400, null)
                }
            }


            override fun enqueue(callback: Callback<List<Track>>) {
                try {
                    callback.onResponse(this, execute())
                } catch (e: Exception) {
                    callback.onFailure(this, e)
                }
            }

            override fun isExecuted() = false
            override fun cancel() {}
            override fun isCanceled() = false
            override fun clone(): Call<List<Track>> = this
            override fun request() = null
            override fun timeout(): Timeout {
                return Timeout.NONE

            }
        }
    }
}