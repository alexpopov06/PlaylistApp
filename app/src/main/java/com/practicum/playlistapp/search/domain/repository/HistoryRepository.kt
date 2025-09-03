package com.practicum.playlistapp.search.domain.repository

import com.practicum.playlistapp.search.domain.model.Track

interface HistoryRepository {
    fun addTrackToHistory(track: Track)
    fun getHistory():List<Track>
    fun clearHistory()

}