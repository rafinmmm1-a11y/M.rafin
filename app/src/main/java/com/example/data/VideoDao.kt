package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    // Videos Queries
    @Query("SELECT * FROM videos ORDER BY isCustom DESC, id ASC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE category = :category ORDER BY id ASC")
    fun getVideosByCategory(category: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isFavorite = 1")
    fun getFavoriteVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE title LIKE :query OR channelName LIKE :query OR category LIKE :query")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("UPDATE videos SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    // Comments Queries
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    // Playlists
    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistVideoCrossRef(crossRef: PlaylistVideoCrossRef)

    @Query("DELETE FROM playlist_videos WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun removeVideoFromPlaylist(playlistId: Int, videoId: String)

    @Query("SELECT * FROM videos INNER JOIN playlist_videos ON videos.id = playlist_videos.videoId WHERE playlist_videos.playlistId = :playlistId")
    fun getVideosInPlaylist(playlistId: Int): Flow<List<VideoEntity>>

    // Watch History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(history: WatchHistoryEntity)

    // Watch History with Video details (Join query)
    @Query("SELECT v.* FROM watch_history h INNER JOIN videos v ON h.videoId = v.id ORDER BY h.viewedAt DESC")
    fun getWatchHistory(): Flow<List<VideoEntity>>

    @Query("DELETE FROM watch_history")
    suspend fun clearWatchHistory()
}
