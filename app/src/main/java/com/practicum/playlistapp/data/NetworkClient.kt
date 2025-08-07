package com.practicum.playlistapp.data

import com.practicum.playlistapp.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}