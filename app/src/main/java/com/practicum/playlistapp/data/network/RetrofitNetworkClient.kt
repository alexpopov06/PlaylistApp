package com.practicum.playlistapp.data.network

import com.practicum.playlistapp.data.NetworkClient
import com.practicum.playlistapp.data.dto.Response
import com.practicum.playlistapp.data.dto.SearchTracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {
    private val BaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val movieApi = retrofit.create(TrackInterface::class.java)
    override fun doRequest(dto: Any): Response {
        if (dto is SearchTracksRequest){
            val resp = movieApi.search(dto.expression).execute()
            val body = resp.body()?: Response()
            return body.apply{resultCode = resp.code()}
        }else{
            return Response().apply{resultCode=400}
        }
    }
}