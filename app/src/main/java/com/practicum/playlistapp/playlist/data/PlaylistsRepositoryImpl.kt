package com.practicum.playlistapp.playlist.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistapp.playlist.domain.model.Playlist
import com.practicum.playlistapp.playlist.domain.repository.PlaylistsRepository
import com.practicum.playlistapp.search.data.db.PlaylistTrackEntity
import com.practicum.playlistapp.search.data.db.AppDatabase
import com.practicum.playlistapp.search.data.db.PlaylistEntity
import com.practicum.playlistapp.search.domain.model.Track
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val context: Context,
    private val db: AppDatabase,
    private val gson: Gson
) : PlaylistsRepository {

    private val dao = db.playlistDao()
    private val trackDao = db.playlistTrackDao()

    override suspend fun createPlaylist(name: String, description: String?, coverUri: Uri?): Long {
        val safeName = name.trim()
        val safeDescription = description?.trim()?.takeIf { it.isNotBlank() }
        val coverPath = coverUri?.let { copyCoverToPrivateStorage(it) }

        val entity = PlaylistEntity(
            name = safeName,
            description = safeDescription,
            coverPath = coverPath,
            trackIdsJson = gson.toJson(emptyList<String>()),
            trackCount = 0
        )

        return dao.insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        dao.updatePlaylist(playlist.toEntity(gson))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return dao.getPlaylists().map { list ->
            list.map { it.toDomain(gson) }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val id = track.trackId ?: ""
        if (id.isBlank()) return

        trackDao.insertTrack(track.toPlaylistTrackEntity())

        val updated = playlist.copy(
            trackIds = playlist.trackIds + id,
            trackCount = playlist.trackCount + 1
        )
        dao.updatePlaylist(updated.toEntity(gson))
    }

    private fun copyCoverToPrivateStorage(uri: Uri): String? {
        val dir = File(context.filesDir, "playlist_covers")
        if (!dir.exists()) dir.mkdirs()

        val extension = getExtension(uri) ?: "jpg"
        val file = File(dir, "cover_${System.currentTimeMillis()}.$extension")

        val input = context.contentResolver.openInputStream(uri) ?: return null
        input.use { src ->
            file.outputStream().use { dst ->
                src.copyTo(dst)
            }
        }
        return file.absolutePath
    }

    private fun getExtension(uri: Uri): String? {
        val mime = context.contentResolver.getType(uri) ?: return null
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
    }
}

private fun Playlist.toEntity(gson: Gson): PlaylistEntity {
    return PlaylistEntity(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIdsJson = gson.toJson(trackIds),
        trackCount = trackCount
    )
}

private fun PlaylistEntity.toDomain(gson: Gson): Playlist {
    val type = object : TypeToken<List<String>>() {}.type
    val ids: List<String> = try {
        gson.fromJson<List<String>>(trackIdsJson, type)
    } catch (e: Exception) {
        emptyList()
    }
    return Playlist(
        id = id,
        name = name,
        description = description,
        coverPath = coverPath,
        trackIds = ids,
        trackCount = trackCount
    )
}

private fun Track.toPlaylistTrackEntity(): PlaylistTrackEntity {
    return PlaylistTrackEntity(
        id = this.trackId ?: "",
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        trackId = this.trackId,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl,
        addedAt = System.currentTimeMillis()
    )
}

