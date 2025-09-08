package com.practicum.playlistapp.search.domain.impl

import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newSingleThreadExecutor()

    override fun searchTrack(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        executor.execute {
            when(val resource = repository.searchTrack(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data ?: emptyList(), null)
                }
                is Resource.Error -> {
                    consumer.consume(emptyList(), resource.message)
                }
            }
        }
    }
}