package com.practicum.playlistapp.domain.usecase

import com.practicum.playlistapp.domain.models.Track
import com.practicum.playlistapp.domain.repository.HistoryRepository


class AddToHistoryUseCase(
    private val repository: HistoryRepository
) {
    fun execute(track: Track) = repository.addTrackToHistory(track)
}

class GetHistoryUseCase(
    private val repository: HistoryRepository
) {
    fun execute(): List<Track> = repository.getHistory()
}

class ClearHistoryUseCase(
    private val repository: HistoryRepository
) {
    fun execute() = repository.clearHistory()
}
