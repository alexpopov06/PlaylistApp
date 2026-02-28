package com.practicum.playlistapp.search.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS playlist_table (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    coverPath TEXT,
                    trackIdsJson TEXT NOT NULL,
                    trackCount INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS playlist_track_table (
                    id TEXT NOT NULL,
                    trackName TEXT,
                    artistName TEXT,
                    trackTimeMillis INTEGER NOT NULL,
                    artworkUrl100 TEXT,
                    trackId TEXT,
                    collectionName TEXT,
                    releaseDate TEXT,
                    primaryGenreName TEXT,
                    country TEXT,
                    previewUrl TEXT,
                    addedAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )
        }
    }
}

