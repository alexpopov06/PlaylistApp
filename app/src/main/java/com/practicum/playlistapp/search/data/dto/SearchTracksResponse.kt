package com.practicum.playlistapp.search.data.dto

import com.practicum.playlistapp.search.data.dto.TrackDto

class SearchTracksResponse(val searchType: String,
                           val expression: String,
                           val results: List<TrackDto>) : Response()