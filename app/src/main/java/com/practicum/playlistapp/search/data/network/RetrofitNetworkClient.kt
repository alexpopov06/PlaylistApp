package com.practicum.playlistapp.search.data.network

import com.practicum.playlistapp.search.data.NetworkClient
import com.practicum.playlistapp.search.data.dto.Response
import com.practicum.playlistapp.search.data.dto.SearchTracksRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(TrackInterface::class.java)

    override suspend fun doRequest(dto: Any): Response {

        if (dto !is SearchTracksRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val resp = api.search(dto.expression)
                resp.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply { resultCode = 500 }
            }
        }
    }
}
