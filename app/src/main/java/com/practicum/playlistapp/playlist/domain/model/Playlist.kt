package com.practicum.playlistapp.playlist.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<String>,
    val trackCount: Int
)

