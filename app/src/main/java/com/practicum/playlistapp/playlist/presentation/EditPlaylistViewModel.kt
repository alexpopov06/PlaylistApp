package com.practicum.playlistapp.playlist.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistapp.playlist.domain.api.PlaylistsInteractor
import com.practicum.playlistapp.playlist.domain.model.Playlist
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    playlistsInteractor: PlaylistsInteractor,
    private val playlistId: Long
) : CreatePlaylistViewModel(playlistsInteractor) {

    private val initialPlaylistLiveData = MutableLiveData<Playlist>()
    fun observeInitialPlaylist(): LiveData<Playlist> = initialPlaylistLiveData

    init {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId).first { it != null }
            initialPlaylistLiveData.postValue(playlist!!)
        }
    }

    fun savePlaylist(name: String, description: String?, coverUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylistDetails(playlistId, name, description, coverUri)
            finishedLiveData.postValue(name.trim())
        }
    }
}
