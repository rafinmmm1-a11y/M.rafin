package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VideoRepository(private val videoDao: VideoDao) {

    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val favoriteVideos: Flow<List<VideoEntity>> = videoDao.getFavoriteVideos()
    val allPlaylists: Flow<List<PlaylistEntity>> = videoDao.getAllPlaylists()
    val watchHistory: Flow<List<VideoEntity>> = videoDao.getWatchHistory()

    fun getVideosByCategory(category: String): Flow<List<VideoEntity>> =
        videoDao.getVideosByCategory(category)

    fun searchVideos(query: String): Flow<List<VideoEntity>> =
        videoDao.searchVideos("%$query%")

    suspend fun getVideoById(id: String): VideoEntity? =
        videoDao.getVideoById(id)

    suspend fun insertVideo(video: VideoEntity) =
        videoDao.insertVideo(video)

    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) =
        videoDao.updateFavoriteStatus(id, isFavorite)

    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> =
        videoDao.getCommentsForVideo(videoId)

    suspend fun insertComment(comment: CommentEntity) =
        videoDao.insertComment(comment)

    suspend fun createPlaylist(name: String, description: String = ""): Long =
        videoDao.insertPlaylist(PlaylistEntity(name = name, description = description))

    suspend fun deletePlaylist(playlistId: Int) =
        videoDao.deletePlaylist(playlistId)

    suspend fun addVideoToPlaylist(playlistId: Int, videoId: String) {
        videoDao.insertPlaylistVideoCrossRef(PlaylistVideoCrossRef(playlistId, videoId))
    }

    suspend fun removeVideoFromPlaylist(playlistId: Int, videoId: String) =
        videoDao.removeVideoFromPlaylist(playlistId, videoId)

    fun getVideosInPlaylist(playlistId: Int): Flow<List<VideoEntity>> =
        videoDao.getVideosInPlaylist(playlistId)

    suspend fun addToHistory(videoId: String) {
        videoDao.insertWatchHistory(WatchHistoryEntity(videoId = videoId, viewedAt = System.currentTimeMillis()))
    }

    suspend fun clearHistory() =
        videoDao.clearWatchHistory()

    suspend fun prepopulateDatabaseIfEmpty() {
        val currentVids = allVideos.first()
        if (currentVids.isEmpty()) {
            val seedList = listOf(
                VideoEntity(
                    id = "Ke7g77mRhyo",
                    title = "Apple Vision Pro Review: Tomorrow's Tech Today",
                    channelName = "Marques Brownlee",
                    description = "Apple Vision Pro has been on my face for a week. Here is what it's actually like to live in, work in, and use daily. Is this the future of computing?",
                    publishedAt = "2024-02-06",
                    duration = "29:56",
                    viewCount = "18.5M views",
                    category = "Tech"
                ),
                VideoEntity(
                    id = "H_Z85w1O4iY",
                    title = "Samsung Galaxy S24 Ultra Review: AI Redefined",
                    channelName = "Marques Brownlee",
                    description = "Samsung's newest S24 Ultra brings a flat screen, titanium construction, new optical zoom cameras, and a suite of Galaxy AI features. Let's see if it's worth the trade-up.",
                    publishedAt = "2024-01-31",
                    duration = "15:20",
                    viewCount = "6.2M views",
                    category = "Tech"
                ),
                VideoEntity(
                    id = "hHW1oY26kxQ",
                    title = "lofi hip hop radio - beats to relax/study to 📚",
                    channelName = "Lofi Girl",
                    description = "Welcome to the official Lofi Girl broadcast center. Unwind, study, write, scale code, or sleep to some calm beats.",
                    publishedAt = "LIVE NOW",
                    duration = "LIVE",
                    viewCount = "120M views",
                    category = "Music"
                ),
                VideoEntity(
                    id = "v3y8AIEX_dU",
                    title = "How Big is the Universe? (Scale Comparison)",
                    channelName = "Kurzgesagt – In a Nutshell",
                    description = "The universe is incredibly colossal. Let's trace it, starting from human dimensions, going through systems, clusters, superclusters and all the way to the observable cosmic boundaries.",
                    publishedAt = "2023-11-09",
                    duration = "11:24",
                    viewCount = "15M views",
                    category = "Science"
                ),
                VideoEntity(
                    id = "0S6QDfD5XlU",
                    title = "Grand Theft Auto VI Official Trailer 1",
                    channelName = "Rockstar Games",
                    description = "Grand Theft Auto VI heads to the state of Leonida, home to the neon-soaked streets of Vice City and beyond. Coming 2025. Music: 'Love Is A Long Road' by Tom Petty.",
                    publishedAt = "2023-12-05",
                    duration = "1:30",
                    viewCount = "210M views",
                    category = "Gaming"
                ),
                VideoEntity(
                    id = "kJQP7kiw5Fk",
                    title = "Luis Fonsi - Despacito ft. Daddy Yankee",
                    channelName = "LuisFonsiVEVO",
                    description = "The global hit single that shook music charts everywhere. Experience the vibrant Latin colors and upbeat rhythms record-breaking music video.",
                    publishedAt = "2017-01-13",
                    duration = "4:42",
                    viewCount = "8.4B views",
                    category = "Music"
                ),
                VideoEntity(
                    id = "qg64fIepz8Q",
                    title = "Minecraft Tricky Trials - Official Trailer",
                    channelName = "Minecraft",
                    description = "Explore, fight, and craft in Minecraft 1.21 Tricky Trials! Battle trial chambers, activate vault blocks, and build automators with dynamic crafting logic.",
                    publishedAt = "2024-06-13",
                    duration = "2:15",
                    viewCount = "9.2M views",
                    category = "Gaming"
                ),
                VideoEntity(
                    id = "dQw4w9WgXcQ",
                    title = "Rick Astley - Never Gonna Give You Up",
                    channelName = "Rick Astley Official",
                    description = "The official video for 'Never Gonna Give You Up' by Rick Astley. Essential classic of the internet generation that remains undefeated.",
                    publishedAt = "2009-10-25",
                    duration = "3:32",
                    viewCount = "1.5B views",
                    category = "Music"
                ),
                VideoEntity(
                    id = "h_S3id_E09E",
                    title = "This Sleek Device Actually Controls Real Time!",
                    channelName = "Veritasium",
                    description = "How precision quartz resonators and atomic oscillators work together to synchronize global satellites and telecommunications.",
                    publishedAt = "2024-03-24",
                    duration = "18:40",
                    viewCount = "4.5M views",
                    category = "Science"
                ),
                VideoEntity(
                    id = "vXp6bS_0U3M",
                    title = "Maldives 4K - Celestial Beach Relaxation",
                    channelName = "Paradise Explorer",
                    description = "Relax in the tranquil ambient sounds of crystal clear waters and golden sunlight across heavenly tropical islands of beautiful Maldives.",
                    publishedAt = "2022-08-11",
                    duration = "10:00",
                    viewCount = "8.1M views",
                    category = "Nature"
                ),
                VideoEntity(
                    id = "yZ8k7S_Wl0g",
                    title = "Inside the World's Cleanest Microchip Silicon Rooms!",
                    channelName = "Veritasium",
                    description = "Take a look inside cleanrooms where microchips are printed in vacuum chambers with zero tolerance for microscopic dust specs.",
                    publishedAt = "2021-12-04",
                    duration = "16:11",
                    viewCount = "8.4M views",
                    category = "Tech"
                ),
                VideoEntity(
                    id = "9bZkp7q19f0",
                    title = "PSY - GANGNAM STYLE(강남스타일) M/V",
                    channelName = "officialpsy",
                    description = "The original internet breaking viral hit video. Experience the dance and rhythms that gained international sensation.",
                    publishedAt = "2012-07-15",
                    duration = "4:12",
                    viewCount = "5.1B views",
                    category = "Music"
                ),
                VideoEntity(
                    id = "OPf0YbXqDm0",
                    title = "Mark Ronson - Uptown Funk ft. Bruno Mars",
                    channelName = "MarkRonsonVEVO",
                    description = "Uptown Funk by Mark Ronson featuring Bruno Mars. Sing along to this soulful, energetic rhythm that dominated radio waves worldwide.",
                    publishedAt = "2014-11-19",
                    duration = "4:31",
                    viewCount = "5.2B views",
                    category = "Music"
                ),
                VideoEntity(
                    id = "f_fJ7N4p0-8",
                    title = "ELDEN RING Shadow of the Erdtree - Official Trailer",
                    channelName = "BANDAI NAMCO Europe",
                    description = "Prepare yourself for the expansion of the beloved action role-playing game. Return to the terrifying and gorgeous Lands Between.",
                    publishedAt = "2024-02-21",
                    duration = "3:06",
                    viewCount = "14M views",
                    category = "Gaming"
                ),
                VideoEntity(
                    id = "z8HIs4xL00g",
                    title = "SpaceX Starship Orbital Flight Test Highlights",
                    channelName = "SpaceX",
                    description = "Key dynamic footage of Starship launches, stage separations, and dramatic reentry sequences under rocket physics.",
                    publishedAt = "2024-03-14",
                    duration = "8:45",
                    viewCount = "11M views",
                    category = "Science"
                )
            )
            videoDao.insertVideos(seedList)
            
            // Seed a few initial comments to make video view vibrant!
            val comments = listOf(
                CommentEntity(videoId = "Ke7g77mRhyo", userName = "TechMaster", commentText = "Wow! Marques always produces high tier work. This Vision Pro display is literally gorgeous."),
                CommentEntity(videoId = "Ke7g77mRhyo", userName = "FutureSeeker", commentText = "1st gen product, but the eye tracking and pass-through are crazy."),
                CommentEntity(videoId = "hHW1oY26kxQ", userName = "CodeGrinder", commentText = "Listening to this while coding ViewTube app right now! Extremely chill vibes."),
                CommentEntity(videoId = "hHW1oY26kxQ", userName = "StudyBuddy", commentText = "This lofi radio has helped me pass literally all my exams in college!"),
                CommentEntity(videoId = "0S6QDfD5XlU", userName = "ViceCityStander", commentText = "I have waited over 10 years for this first trailer and it did not disappoint. Visuals are wild!"),
                CommentEntity(videoId = "dQw4w9WgXcQ", userName = "RickFan", commentText = "Still getting rickrolled in 2026. This song is an absolute legendary masterpiece!")
            )
            for (c in comments) {
                videoDao.insertComment(c)
            }
            
            // Seed a default playlist
            val playlistId = videoDao.insertPlaylist(PlaylistEntity(name = "My Favorites", description = "Best videos to watch anytime"))
            videoDao.insertPlaylistVideoCrossRef(PlaylistVideoCrossRef(playlistId.toInt(), "hHW1oY26kxQ"))
            videoDao.insertPlaylistVideoCrossRef(PlaylistVideoCrossRef(playlistId.toInt(), "v3y8AIEX_dU"))
        }
    }
}
