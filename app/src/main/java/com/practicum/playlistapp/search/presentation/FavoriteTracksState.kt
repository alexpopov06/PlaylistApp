package com.practicum.playlistapp.search.presentation

import com.practicum.playlistapp.search.data.db.TrackEntity


import com.practicum.playlistapp.search.domain.model.Track

sealed class FavoriteTracksState {
    object Empty : FavoriteTracksState()
    data class Content(val tracks: List<Track>) : FavoriteTracksState()
}
