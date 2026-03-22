package com.practicum.playlistapp.playlist.domain.repository

import android.net.Uri
import com.practicum.playlistapp.playlist.domain.model.Playlist
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(
        name: String,
        description: String?,
        coverUri: Uri?
    ): Long

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}

