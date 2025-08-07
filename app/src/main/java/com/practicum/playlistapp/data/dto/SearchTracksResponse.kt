package com.practicum.playlistapp.data.dto

class SearchTracksResponse(val searchType: String,
                           val expression: String,
                           val results: List<TrackDto>) : Response()