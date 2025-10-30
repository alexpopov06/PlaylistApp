package com.practicum.playlistapp.search.domain.impl

import android.util.Log
import com.practicum.playlistapp.creator.Resource
import com.practicum.playlistapp.search.domain.repository.TracksRepository
import com.practicum.playlistapp.search.domain.api.TracksInteractor
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newSingleThreadExecutor()
    private val TAG = "TracksInteractorImpl"

    override fun searchTrack(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        Log.e(TAG, "1. searchTrack called with: '$expression'")

        executor.execute {
            Log.e(TAG, "2. Executor started on thread: ${Thread.currentThread().name}")

            try {
                Log.e(TAG, "3. Before repository.searchTrack")

                val resource = repository.searchTrack(expression)
                Log.e(TAG, "4. After repository.searchTrack, resource: $resource")

                when(resource) {
                    is Resource.Success -> {
                        Log.e(TAG, "5. Success - tracks count: ${resource.data?.size ?: 0}")
                        consumer.consume(resource.data ?: emptyList(), null)
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "6. Error - message: ${resource.message}")
                        consumer.consume(emptyList(), resource.message)
                    }
                }

                Log.e(TAG, "7. Search completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "8. EXCEPTION in searchTrack: ${e.message}")

                // ВАЖНО: Немедленно отправляем ошибку НЕ в главном потоке
                // чтобы приложение не успело упасть
                consumer.consume(emptyList(), "NETWORK_ERROR: ${e.message}")
            }
        }

        Log.e(TAG, "9. searchTrack method finished")
    }
}