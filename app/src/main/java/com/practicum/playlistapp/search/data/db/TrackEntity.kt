package com.practicum.playlistapp.search.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistapp.search.domain.model.Track.Companion.formatMillisToMmSs

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey
    val id: String,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String,
    val formattedDuration: String = formatMillisToMmSs(trackTimeMillis),
    val addedAt: Long = System.currentTimeMillis()


)
