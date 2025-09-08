package com.practicum.playlistapp.search.domain.usecase

import com.practicum.playlistapp.search.domain.model.Track
import com.practicum.playlistapp.search.domain.repository.HistoryRepository


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
