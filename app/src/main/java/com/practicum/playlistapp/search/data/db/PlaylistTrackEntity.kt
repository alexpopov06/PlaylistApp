package com.practicum.playlistapp.search.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_track_table")
data class PlaylistTrackEntity(
    @PrimaryKey val id: String,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val trackId: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val addedAt: Long
)

