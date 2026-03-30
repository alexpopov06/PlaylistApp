package com.practicum.playlistapp.playlist.domain.impl

import android.net.Uri
import com.practicum.playlistapp.playlist.domain.api.PlaylistsInteractor
import com.practicum.playlistapp.playlist.domain.model.Playlist
import com.practicum.playlistapp.playlist.domain.repository.PlaylistsRepository
import com.practicum.playlistapp.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {
    override suspend fun createPlaylist(name: String, description: String?, coverUri: Uri?): Long {
        return repository.createPlaylist(name, description, coverUri)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun updatePlaylistDetails(
        playlistId: Long,
        name: String,
        description: String?,
        coverUri: Uri?
    ) {
        repository.updatePlaylistDetails(playlistId, name, description, coverUri)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override fun getPlaylistById(id: Long): Flow<Playlist?> {
        return repository.getPlaylistById(id)
    }

    override fun getTracksForPlaylistTrackIds(trackIds: List<String>): Flow<List<Track>> {
        return repository.getTracksForPlaylistTrackIds(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        repository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }
}

