package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CommentEntity
import com.example.data.PlaylistEntity
import com.example.data.VideoEntity
import com.example.data.VideoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoRepository

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")

    private val _currentPlayingVideo = MutableStateFlow<VideoEntity?>(null)
    val currentPlayingVideo: StateFlow<VideoEntity?> = _currentPlayingVideo.asStateFlow()

    // Reactive comments stream for currently selected video
    val currentVideoComments: StateFlow<List<CommentEntity>> = _currentPlayingVideo
        .flatMapLatest { video ->
            if (video == null) flowOf(emptyList())
            else repository.getCommentsForVideo(video.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VideoRepository(database.videoDao())

        // Prepopulate video library asynchronously
        viewModelScope.launch {
            repository.prepopulateDatabaseIfEmpty()
        }
    }

    val playlists: StateFlow<List<PlaylistEntity>> = repository.allPlaylists
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val watchHistory: StateFlow<List<VideoEntity>> = repository.watchHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteVideos: StateFlow<List<VideoEntity>> = repository.favoriteVideos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Master list of videos
    val allVideos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reactively combined list based on category and search query
    val filteredVideos: StateFlow<List<VideoEntity>> = combine(
        repository.allVideos,
        searchQuery,
        selectedCategory
    ) { videos, query, category ->
        var list = videos
        
        // Apply Category Filter
        if (category != "All") {
            list = if (category == "Favorites") {
                list.filter { it.isFavorite }
            } else {
                list.filter { it.category == category }
            }
        }

        // Apply Search Query Filter
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.channelName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }
        
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectVideo(video: VideoEntity) {
        _currentPlayingVideo.value = video
        viewModelScope.launch {
            repository.addToHistory(video.id)
        }
    }

    fun deselectVideo() {
        _currentPlayingVideo.value = null
    }

    fun toggleFavorite(video: VideoEntity) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(video.id, !video.isFavorite)
            // Update currently playing video if it matches to update state in player view
            if (_currentPlayingVideo.value?.id == video.id) {
                _currentPlayingVideo.value = _currentPlayingVideo.value?.copy(isFavorite = !video.isFavorite)
            }
        }
    }

    fun addComment(videoId: String, userName: String, commentText: String) {
        if (commentText.isBlank()) return
        viewModelScope.launch {
            val name = if (userName.trim().isEmpty()) "Anonymous" else userName.trim()
            repository.insertComment(
                CommentEntity(
                    videoId = videoId,
                    userName = name,
                    commentText = commentText.trim()
                )
            )
        }
    }

    fun createPlaylist(name: String, description: String = "") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createPlaylist(name.trim(), description.trim())
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun addVideoToPlaylist(playlistId: Int, videoId: String) {
        viewModelScope.launch {
            repository.addVideoToPlaylist(playlistId, videoId)
        }
    }

    fun removeVideoFromPlaylist(playlistId: Int, videoId: String) {
        viewModelScope.launch {
            repository.removeVideoFromPlaylist(playlistId, videoId)
        }
    }

    fun getVideosInPlaylist(playlistId: Int): Flow<List<VideoEntity>> {
        return repository.getVideosInPlaylist(playlistId)
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun addCustomVideo(
        urlOrId: String,
        title: String,
        channel: String,
        category: String,
        duration: String,
        description: String
    ): Boolean {
        val extractedId = extractYoutubeId(urlOrId)
        if (extractedId.isBlank()) return false

        viewModelScope.launch {
            val customVideo = VideoEntity(
                id = extractedId,
                title = if (title.isBlank()) "Custom Video ($extractedId)" else title.trim(),
                channelName = if (channel.isBlank()) "User Added" else channel.trim(),
                description = if (description.isBlank()) "Pasted YouTube Video added by the user." else description.trim(),
                publishedAt = "Just Now",
                duration = if (duration.isBlank()) "3:00" else duration.trim(),
                viewCount = "1 view",
                category = if (category == "All" || category == "Favorites") "Tech" else category,
                isCustom = true
            )
            repository.insertVideo(customVideo)
        }
        return true
    }

    fun extractYoutubeId(input: String): String {
        val trimmed = input.trim()
        if (trimmed.length == 11 && !trimmed.contains("/") && !trimmed.contains("?")) return trimmed

        // Robust matching patterns
        val regex = "v=([a-zA-Z0-9_-]{11})|/v/([a-zA-Z0-9_-]{11})|youtu\\.be/([a-zA-Z0-9_-]{11})|/embed/([a-zA-Z0-9_-]{11})|shorts/([a-zA-Z0-9_-]{11})|be/([a-zA-Z0-9_-]{11})".toRegex()
        val match = regex.find(trimmed)
        if (match != null) {
            for (group in match.groups.drop(1)) {
                if (group != null) return group.value
            }
        }
        
        // Guess if it's a URL but we didn't match group, try splitting
        if (trimmed.contains("?")) {
            val queryParams = trimmed.substringAfter("?").split("&")
            for (param in queryParams) {
                if (param.startsWith("v=")) {
                    val idVal = param.substring(2)
                    if (idVal.length == 11) return idVal
                }
            }
        }
        
        // Try getting last path segment
        if (trimmed.contains("/")) {
            val lastSegment = trimmed.substringAfterLast("/")
            val cleanId = lastSegment.substringBefore("?")
            if (cleanId.length == 11) return cleanId
        }

        return trimmed
    }
}
