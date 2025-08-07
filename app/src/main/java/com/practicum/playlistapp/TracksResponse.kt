package com.practicum.playlistapp

import com.practicum.playlistapp.domain.models.Track

class TracksResponse(val resultCount: Int,
    val results: List<Track>)