package com.practicum.playlistapp.playlist.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistapp.playlist.domain.api.PlaylistsInteractor
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    protected val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    protected val finishedLiveData = MutableLiveData<String>()
    fun observeFinished(): LiveData<String> = finishedLiveData

    open fun createPlaylist(name: String, description: String?, coverUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.createPlaylist(name, description, coverUri)
            finishedLiveData.postValue(name.trim())
        }
    }
}
