package com.practicum.playlistapp.search.data.network

import com.practicum.playlistapp.search.data.dto.SearchTracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackInterface {

    @GET("/search?entity=song")
    suspend fun search(
        @Query("term") text: String
    ): SearchTracksResponse
}
