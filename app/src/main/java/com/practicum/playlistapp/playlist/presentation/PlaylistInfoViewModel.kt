package com.practicum.playlistapp.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistapp.playlist.domain.api.PlaylistsInteractor
import com.practicum.playlistapp.playlist.domain.model.Playlist
import com.practicum.playlistapp.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistId: Long,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val playlistLiveData = MutableLiveData<Playlist?>()
    fun observePlaylist(): LiveData<Playlist?> = playlistLiveData

    private val totalDurationTextLiveData = MutableLiveData<String>()
    fun observeTotalDurationText(): LiveData<String> = totalDurationTextLiveData

    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun observeTracks(): LiveData<List<Track>> = tracksLiveData

    private val playlistDeletedLiveData = MutableLiveData<Unit>()
    fun observePlaylistDeleted(): LiveData<Unit> = playlistDeletedLiveData

    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId).collectLatest { playlist ->
                playlistLiveData.postValue(playlist)
                if (playlist == null) {
                    tracksLiveData.postValue(emptyList())
                    totalDurationTextLiveData.postValue(
                        SimpleDateFormat("mm", Locale.getDefault()).format(Date(0L))
                    )
                } else {
                    playlistsInteractor.getTracksForPlaylistTrackIds(playlist.trackIds)
                        .collect { tracks ->
                            tracksLiveData.postValue(tracks)
                            val durationSum = tracks.sumOf { it.trackTimeMillis }
                            totalDurationTextLiveData.postValue(
                                SimpleDateFormat("mm", Locale.getDefault()).format(Date(durationSum))
                            )
                        }
                }
            }
        }
    }

    fun removeTrackFromPlaylist(trackId: String) {
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)
            playlistDeletedLiveData.postValue(Unit)
        }
    }
}
