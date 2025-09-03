package com.practicum.playlistapp.search.data

import com.practicum.playlistapp.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}