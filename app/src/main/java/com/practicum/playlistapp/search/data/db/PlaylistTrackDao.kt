package com.practicum.playlistapp.search.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table")
    fun getAllTracks(): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_track_table WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: String)
}

