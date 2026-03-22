package com.practicum.playlistapp.playlist.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistapp.playlist.domain.api.PlaylistsInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val createdLiveData = MutableLiveData<String>()
    fun observeCreated(): LiveData<String> = createdLiveData

    fun createPlaylist(name: String, description: String?, coverUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.createPlaylist(name, description, coverUri)
            createdLiveData.postValue(name)
        }
    }
}

