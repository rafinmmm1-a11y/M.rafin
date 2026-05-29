package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.CommentEntity
import com.example.data.PlaylistEntity
import com.example.data.VideoEntity
import com.example.ui.components.YoutubePlayer
import com.example.viewmodel.VideoViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUiContainer(viewModel: VideoViewModel) {
    val context = LocalContext.current
    val currentTab = remember { mutableStateOf("home") }
    val currentlyPlaying by viewModel.currentPlayingVideo.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Logo Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ViewTube",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Premium",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.Top)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F0F0F),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F0F0F),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab.value == "home",
                    onClick = { currentTab.value = "home" },
                    icon = {
                        Icon(
                            imageVector = if (currentTab.value == "home") Icons.Default.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color(0xFF251A1A),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = currentTab.value == "library",
                    onClick = { currentTab.value = "library" },
                    icon = {
                        Icon(
                            imageVector = if (currentTab.value == "library") Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary,
                            contentDescription = "Library"
                        )
                    },
                    label = { Text("Library", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color(0xFF251A1A),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_library")
                )
                NavigationBarItem(
                    selected = currentTab.value == "history",
                    onClick = { currentTab.value = "history" },
                    icon = {
                        Icon(
                            imageVector = if (currentTab.value == "history") Icons.Default.History else Icons.Outlined.History,
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color(0xFF251A1A),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_history")
                )
            }
        },
        containerColor = Color(0xFF0F0F0F)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab.value) {
                "home" -> HomeTabContent(viewModel = viewModel)
                "library" -> LibraryTabContent(viewModel = viewModel)
                "history" -> HistoryTabContent(viewModel = viewModel)
            }

            // Beautiful Full Detail Overlay Player Panel
            AnimatedVisibility(
                visible = currentlyPlaying != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                currentlyPlaying?.let { video ->
                    VideoDetailScreen(
                        video = video,
                        viewModel = viewModel,
                        onBack = { viewModel.deselectVideo() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTabContent(viewModel: VideoViewModel) {
    val videos by viewModel.filteredVideos.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val controller = LocalSoftwareKeyboardController.current

    val categories = listOf("All", "Tech", "Music", "Gaming", "Science", "Nature", "Favorites")

    Column(modifier = Modifier.fillMaxSize()) {
        // High-fidelity search bar
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search videos, creators, categories...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("video_search_bar"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1F1F1F),
                unfocusedContainerColor = Color(0xFF1F1F1F),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { controller?.hide() }),
            singleLine = true
        )

        // Pill categories
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color(0xFF1F1F1F)
                        )
                        .clickable { viewModel.selectedCategory.value = category }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else Color(0xFFF1F1F1),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Grid of videos
        if (videos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No results",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No videos found",
                        color = Color.LightGray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing keywords or choosing another category",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(videos, key = { it.id }) { video ->
                    VideoGridCard(video = video, onClick = { viewModel.selectVideo(video) })
                }
            }
        }
    }
}

@Composable
fun VideoGridCard(video: VideoEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("video_card_${video.id}")
    ) {
        // Thumbnail & Duration Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.DarkGray)
        ) {
            // Real YouTube High Quality Max Thumbnail Loading!
            AsyncImage(
                model = "https://img.youtube.com/vi/${video.id}/mqdefault.jpg",
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic Gradient Overlay for dark look
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0x33000000)),
                            startY = 100f
                        )
                    )
            )

            // Duration banner bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = video.duration,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Metadata block (Title, Channel, Info, Favorites Icon)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Channel Avatar placeholder
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF033E),
                                Color(0xFF8B0000)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.channelName.firstOrNull()?.toString() ?: "V",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.channelName,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = video.viewCount,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Small bookmark/favorite dot indicator
            if (video.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorited",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTabContent(viewModel: VideoViewModel) {
    val context = LocalContext.current
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Form inputs
    var videoUrl by remember { mutableStateOf("") }
    var videoTitle by remember { mutableStateOf("") }
    var videoChannel by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("Tech") }
    var isFormExpanded by remember { mutableStateOf(false) }

    // Playlist dialog
    var showDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var newPlaylistDesc by remember { mutableStateOf("") }

    // Active playlist contents viewer state
    var selectedPlaylistForView by remember { mutableStateOf<PlaylistEntity?>(null) }
    val playlistVideosState = remember(selectedPlaylistForView) {
        selectedPlaylistForView?.let { viewModel.getVideosInPlaylist(it.id) }
    }
    val playlistVideos by (playlistVideosState?.collectAsStateWithLifecycle(emptyList()) ?: remember { mutableStateOf(emptyList()) })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Option 1: Pasting Youtube Link
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFormExpanded = !isFormExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Pasted Url icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Add Custom YouTube Video",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                        Icon(
                            imageVector = if (isFormExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = Color.Gray
                        )
                    }

                    if (isFormExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Paste any YouTube watch link, shorts, embed, or raw 11-char video ID to watch it instantly in ViewTube!",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            label = { Text("YouTube URL or Video ID") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_video_url"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = videoTitle,
                            onValueChange = { videoTitle = it },
                            label = { Text("Video Title (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = videoChannel,
                            onValueChange = { videoChannel = it },
                            label = { Text("Channel Name (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Category choice dropdown
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Category: ", fontWeight = FontWeight.SemiBold, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            val categories = listOf("Tech", "Music", "Gaming", "Science", "Nature")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(categories) { cat ->
                                    val isSelected = cat == customCategory
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF282828))
                                            .clickable { customCategory = cat }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(cat, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (videoUrl.isBlank()) {
                                    Toast.makeText(context, "Please enter a URL or ID", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val id = viewModel.extractYoutubeId(videoUrl)
                                if (id.isBlank() || id.length != 11) {
                                    Toast.makeText(context, "Invalid 11-character video ID detected", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val success = viewModel.addCustomVideo(
                                    urlOrId = videoUrl,
                                    title = videoTitle,
                                    channel = videoChannel,
                                    category = customCategory,
                                    duration = "Custom",
                                    description = "Custom video pasted into library."
                                )
                                if (success) {
                                    Toast.makeText(context, "Added custom video!", Toast.LENGTH_SHORT).show()
                                    // Reset fields
                                    videoUrl = ""
                                    videoTitle = ""
                                    videoChannel = ""
                                    isFormExpanded = false
                                } else {
                                    Toast.makeText(context, "Failed to analyze YouTube structure", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_custom_video_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayCircleFilled, contentDescription = "Play icon")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import and Play", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Playlists header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Custom Playlists",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
                Button(
                    onClick = { showDialog = true },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F1F)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New", tint = Color.White, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Playlists list
        if (playlists.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No playlists yet. Create one above!", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            items(playlists) { playlist ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedPlaylistForView = playlist },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPlaylistForView?.id == playlist.id) Color(0xFF282828) else Color(0xFF141414)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2B2B2B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FeaturedPlayList,
                                    contentDescription = "Playlist",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(playlist.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                if (playlist.description.isNotEmpty()) {
                                    Text(playlist.description, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.deletePlaylist(playlist.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Playlist", tint = Color.Gray)
                        }
                    }
                }
            }
        }

        // Viewer pane for selected playlist
        selectedPlaylistForView?.let { playlist ->
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Divider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Videos in: ${playlist.name}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${playlistVideos.size} videos total",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    IconButton(onClick = { selectedPlaylistForView = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Playlist view", tint = Color.LightGray)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (playlistVideos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("This playlist is empty. Save videos to this playlist while watching!", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                items(playlistVideos) { v ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectVideo(v) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F0F)),
                        border = BorderStroke(1.dp, Color(0xFF1F1F1F))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = "https://img.youtube.com/vi/${v.id}/mqdefault.jpg",
                                contentDescription = v.title,
                                modifier = Modifier
                                    .width(80.dp)
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(v.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(v.channelName, color = Color.Gray, fontSize = 11.sp)
                            }

                            IconButton(onClick = { viewModel.removeVideoFromPlaylist(playlist.id, v.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove video", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog to create playlist
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Create New Playlist") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Playlist Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPlaylistDesc,
                        onValueChange = { newPlaylistDesc = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            viewModel.createPlaylist(newPlaylistName, newPlaylistDesc)
                            newPlaylistName = ""
                            newPlaylistDesc = ""
                            showDialog = false
                            Toast.makeText(context, "Playlist created!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryTabContent(viewModel: VideoViewModel) {
    val history by viewModel.watchHistory.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Watched",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            )
            if (history.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearHistory() }) {
                    Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear all", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No history",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your watch history is empty",
                        color = Color.LightGray,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(history) { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectVideo(video) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(110.dp)
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.DarkGray)
                            ) {
                                AsyncImage(
                                    model = "https://img.youtube.com/vi/${video.id}/mqdefault.jpg",
                                    contentDescription = video.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .background(Color.Black.copy(alpha = 0.8f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(video.duration, color = Color.White, fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = video.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = video.channelName,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    video: VideoEntity,
    viewModel: VideoViewModel,
    onBack: () -> Unit
) {
    val comments by viewModel.currentVideoComments.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    // Custom comment text field state
    var newCommentText by remember { mutableStateOf("") }
    var commenterName by remember { mutableStateOf("") }

    // Playlist selector menu sheet state
    var showPlaylistSelector by remember { mutableStateOf(false) }

    // Handle back presses inside state details properly!
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        // Player header bar back press button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(vertical = 4.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("player_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Playing: ${video.title}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        // Gorgeous custom WebView interactive Youtube Player in full horizontal bleed!
        YoutubePlayer(
            videoId = video.id,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
        )

        // Metadata & Actions Panel scrolling list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.7f)
                .background(Color(0xFF0F0F0F)),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Video stats block
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 23.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${video.viewCount} • Uploaded in ${video.publishedAt}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Interactive Red Subscribe & Action buttons Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Creator avatar info block
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCC0000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(video.channelName.firstOrNull()?.toString() ?: "Y", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(video.channelName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("1.2M Subscribers", color = Color.Gray, fontSize = 11.sp)
                        }
                    }

                    // Bold mockup subscribe button
                    var isSubscribed by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            isSubscribed = !isSubscribed
                            val msg = if (isSubscribed) "Subscribed to ${video.channelName}!" else "Unsubscribed"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubscribed) Color(0xFF282828) else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 5.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = if (isSubscribed) "Subscribed" else "Subscribe",
                            color = if (isSubscribed) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Secondary functional button rows (Like, Save, Share)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Favorite button
                    Button(
                        onClick = { viewModel.toggleFavorite(video) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F1F)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (video.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (video.isFavorite) MaterialTheme.colorScheme.primary else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Favorite", color = Color.White, fontSize = 12.sp)
                    }

                    // Save to custom playlist button (opens sheet)
                    Button(
                        onClick = { showPlaylistSelector = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F1F)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_to_playlist_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "Playlist",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save To...", color = Color.White, fontSize = 12.sp)
                    }

                    // Copy Link / Share
                    Button(
                        onClick = {
                            val link = "https://youtu.be/${video.id}"
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, link)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Share Link"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F1F)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Description Collapsible Container
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { isDescriptionExpanded = !isDescriptionExpanded },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Description", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = video.description,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            maxLines = if (isDescriptionExpanded) 100 else 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isDescriptionExpanded) "Show less" else "...more",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Live functional Comments Section Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Divider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Comments (${comments.size})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Comment Write Inputs Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = commenterName,
                                    onValueChange = { commenterName = it },
                                    label = { Text("Your Name", fontSize = 11.sp) },
                                    modifier = Modifier.weight(0.4f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = Color.DarkGray
                                    ),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = newCommentText,
                                    onValueChange = { newCommentText = it },
                                    label = { Text("Add public comment...", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .testTag("comment_input_field"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = Color.DarkGray
                                    ),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        viewModel.addComment(
                                            videoId = video.id,
                                            userName = commenterName,
                                            commentText = newCommentText
                                        )
                                        newCommentText = ""
                                        commenterName = ""
                                        Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("comment_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Post Comment", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Real Comment Entries list
            if (comments.isEmpty()) {
                item {
                    Text(
                        text = "No comments yet. Be the first to comment!",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            } else {
                items(comments) { comment ->
                    CommentItemView(comment)
                }
            }
        }
    }

    // Modal playlist picker for adding the video
    if (showPlaylistSelector) {
        AlertDialog(
            onDismissRequest = { showPlaylistSelector = false },
            title = { Text("Add video to playlist", color = Color.White) },
            containerColor = Color(0xFF161616),
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (playlists.isEmpty()) {
                        Text("No playlists configured in library.", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(playlists) { pl ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addVideoToPlaylist(pl.id, video.id)
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Added to ${pl.name}!",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            showPlaylistSelector = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF282828))
                                ) {
                                    Text(
                                        text = pl.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPlaylistSelector = false }) {
                    Text("Close", color = Color.LightGray)
                }
            }
        )
    }
}

@Composable
fun CommentItemView(comment: CommentEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E2E2E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (comment.userName.firstOrNull() ?: "?").toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "just now",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.commentText,
                color = Color.LightGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
