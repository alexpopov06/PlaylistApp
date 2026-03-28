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

    suspend fun updatePlaylistDetails(
        playlistId: Long,
        name: String,
        description: String?,
        coverUri: Uri?
    )

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun getPlaylistById(id: Long): Flow<Playlist?>

    fun getTracksForPlaylistTrackIds(trackIds: List<String>): Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String)

    suspend fun deletePlaylist(playlistId: Long)
}

