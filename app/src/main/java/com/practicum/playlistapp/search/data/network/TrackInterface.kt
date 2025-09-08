package com.practicum.playlistapp.search.data.network

import com.practicum.playlistapp.search.data.dto.SearchTracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackInterface {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SearchTracksResponse>
}