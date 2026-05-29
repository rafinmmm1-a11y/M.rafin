package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String, // YouTube Video ID
    val title: String,
    val channelName: String,
    val description: String,
    val publishedAt: String,
    val duration: String,
    val viewCount: String,
    val category: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: String,
    val userName: String,
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = ""
)

@Entity(tableName = "playlist_videos", primaryKeys = ["playlistId", "videoId"])
data class PlaylistVideoCrossRef(
    val playlistId: Int,
    val videoId: String
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: String,
    val viewedAt: Long = System.currentTimeMillis()
)
