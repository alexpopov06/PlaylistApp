package com.practicum.playlistapp.data.repositoryImpl

import com.practicum.playlistapp.data.NetworkClient
import com.practicum.playlistapp.data.dto.SearchTracksRequest
import com.practicum.playlistapp.data.dto.SearchTracksResponse
import com.practicum.playlistapp.domain.models.Track
import com.practicum.playlistapp.domain.repository.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchTracksRequest(expression))
        if (response.resultCode == 200) {
            return (response as SearchTracksResponse).results.map {
                Track(
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeMillis,
                    artworkUrl100 = it.artworkUrl100,
                    trackId = it.trackId?.toString(),
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }
}