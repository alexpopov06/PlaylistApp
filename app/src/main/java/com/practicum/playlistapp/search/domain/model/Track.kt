package com.practicum.playlistapp.search.domain.model

data class Track(
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
    val formattedDuration: String = formatMillisToMmSs(trackTimeMillis)
) {
    companion object {
        fun formatMillisToMmSs(millis: Long): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
    }
}