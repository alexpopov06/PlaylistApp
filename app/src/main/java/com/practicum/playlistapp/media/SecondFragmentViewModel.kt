package com.practicum.playlistapp.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.practicum.playlistapp.playlist.domain.api.PlaylistsInteractor
import com.practicum.playlistapp.playlist.domain.model.Playlist

class SecondFragmentViewModel(
    playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    val playlists: LiveData<List<Playlist>> = playlistsInteractor.getPlaylists().asLiveData()
}