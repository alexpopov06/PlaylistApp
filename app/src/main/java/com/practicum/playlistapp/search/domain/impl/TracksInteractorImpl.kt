package com.practicum.playlistapp.search.domain.impl

import android.os.Handler
import android.os.Looper
import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import com.practicum.playlistapp.search.domain.api.TracksInteractor

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    private val handler = Handler(Looper.getMainLooper())

    override fun searchTrack(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        val t = Thread {
            when(val resource = repository.searchTrack(expression)) {
                is Resource.Success -> {
                    handler.post {
                        consumer.consume(resource.data ?: emptyList(), null)
                    }
                }
                is Resource.Error -> {

                    handler.post {
                        consumer.consume(emptyList(), resource.message)
                    }
                }
            }
        }
        t.start()
    }
}