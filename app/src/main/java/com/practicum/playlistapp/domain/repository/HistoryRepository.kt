package com.practicum.playlistapp.domain.repository

import com.practicum.playlistapp.domain.models.Track

interface HistoryRepository {
    fun addTrackToHistory(track: Track)
    fun getHistory():List<Track>
    fun clearHistory()

}