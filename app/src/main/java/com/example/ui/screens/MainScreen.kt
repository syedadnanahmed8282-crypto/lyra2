package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.EqualizerDialog
import com.example.ui.components.MediaDetailSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.SleepTimerDialog
import com.example.ui.components.SortMenu
import com.example.ui.theme.DarkPurpleSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassBorderHighlight
import com.example.ui.theme.FrostedGlassCard
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftPurpleBg
import com.example.ui.theme.SunnyYellowBg
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantPurple
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.PlayerViewModel

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.screens.HomeTab
import com.example.ui.screens.ProfileTab

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    playerViewModel: PlayerViewModel
) {
    var bottomNavIndex by remember { mutableStateOf(0) }

    val selectedTab by mainViewModel.selectedTab.collectAsState()
    val songs by mainViewModel.songs.collectAsState()
    val albums by mainViewModel.albums.collectAsState()
    val artists by mainViewModel.artists.collectAsState()
    val playlists by mainViewModel.playlists.collectAsState()
    val playlistItems by mainViewModel.playlistItems.collectAsState()
    val folders by mainViewModel.folders.collectAsState()

    val searchQuery by mainViewModel.searchQuery.collectAsState()
    val sortOrder by mainViewModel.sortOrder.collectAsState()
    val themeColor by mainViewModel.themeColor.collectAsState()
    val selectedExtensionMode by mainViewModel.selectedExtensionMode.collectAsState()
    val extensionAccounts by mainViewModel.extensionAccounts.collectAsState()

    val currentSong by playerViewModel.currentSong.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val isLoading by playerViewModel.isLoading.collectAsState()
    val currentPositionMs by playerViewModel.currentPosition.collectAsState()
    val durationMs by playerViewModel.duration.collectAsState()
    val repeatMode by playerViewModel.repeatMode.collectAsState()
    val isShuffle by playerViewModel.isShuffle.collectAsState()
    val sleepTimerMsLeft by playerViewModel.sleepTimerMsLeft.collectAsState()
    val equalizerInfo by playerViewModel.equalizerInfo.collectAsState()

    val isNowPlayingExpanded by playerViewModel.isNowPlayingExpanded.collectAsState()
    val showEqualizerDialog by playerViewModel.showEqualizerDialog.collectAsState()
    val showSleepTimerDialog by playerViewModel.showSleepTimerDialog.collectAsState()

    val songToAddToPlaylist by mainViewModel.songToAddToPlaylist.collectAsState()
    val showCreatePlaylistDialog by mainViewModel.showCreatePlaylistDialog.collectAsState()
    val mediaDetailState by mainViewModel.mediaDetailState.collectAsState()

    val plugins by mainViewModel.installedPlugins.collectAsState()
    val searchResults by mainViewModel.onlineSearchResults.collectAsState()
    val isSearchingOnline by mainViewModel.isSearchingOnline.collectAsState()

    var showAudioSettingsDialog by remember { mutableStateOf(false) }
    var showManageExtensionsDialogBySettings by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "extension.js"
            mainViewModel.installPluginFromLocalUri(uri, fileName) { success, error ->
                if (success) {
                    Toast.makeText(context, "Extension imported successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Import failed: $error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val tabs = listOf("Songs", "Albums", "Artists", "Playlists", "Folders")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.SpotifyDarkCanvas)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main Content Body based on Bottom Nav Bar Selection
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (bottomNavIndex) {
                    0 -> {
                        // 0: HOME SCREEN (Matches Spotify exact design)
                        HomeTab(
                            songs = songs,
                            albums = albums,
                            artists = artists,
                            onSongClick = { list, index -> playerViewModel.playSongList(list, index) },
                            onArtistClick = { artist, artistSongs ->
                                mainViewModel.openMediaDetail(
                                    type = "ARTIST",
                                    title = artist.name,
                                    subtitle = "${artistSongs.size} Songs",
                                    artworkUri = null,
                                    initialSongs = artistSongs
                                )
                            },
                            onAlbumClick = { album, albumSongs ->
                                mainViewModel.openMediaDetail(
                                    type = "ALBUM",
                                    title = album.title,
                                    subtitle = album.artist,
                                    artworkUri = album.albumArtUri,
                                    initialSongs = albumSongs
                                )
                            },
                            onOpenSettings = { showAudioSettingsDialog = true }
                        )
                    }

                    1 -> {
                        // 1: SEARCH SCREEN
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Search Header
                            Text(
                                text = "Search",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = com.example.ui.theme.SpotifyTextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )

                            // Search Bar Input
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { mainViewModel.setSearchQuery(it) },
                                placeholder = { Text("What do you want to listen to?", color = com.example.ui.theme.SpotifyTextSecondary, fontSize = 14.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = com.example.ui.theme.SpotifyGreen) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { mainViewModel.setSearchQuery("") }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = com.example.ui.theme.SpotifyTextSecondary)
                                        }
                                    }
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = com.example.ui.theme.SpotifyCardBg,
                                    unfocusedContainerColor = com.example.ui.theme.SpotifyCardBg,
                                    focusedTextColor = com.example.ui.theme.SpotifyTextPrimary,
                                    unfocusedTextColor = com.example.ui.theme.SpotifyTextPrimary,
                                    focusedIndicatorColor = com.example.ui.theme.SpotifyGreen,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .testTag("search_bar")
                            )

                            // Sources Filter Chips
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    FilterChip(
                                        selected = selectedExtensionMode == "ALL",
                                        onClick = { mainViewModel.setSelectedExtensionMode("ALL") },
                                        label = { Text("🌐 All Sources", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = com.example.ui.theme.SpotifyGreen,
                                            selectedLabelColor = Color.Black,
                                            containerColor = com.example.ui.theme.SpotifyCardBg,
                                            labelColor = com.example.ui.theme.SpotifyTextPrimary
                                        )
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedExtensionMode == "LOCAL",
                                        onClick = { mainViewModel.setSelectedExtensionMode("LOCAL") },
                                        label = { Text("📱 Local Only", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = com.example.ui.theme.SpotifyGreen,
                                            selectedLabelColor = Color.Black,
                                            containerColor = com.example.ui.theme.SpotifyCardBg,
                                            labelColor = com.example.ui.theme.SpotifyTextPrimary
                                        )
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = selectedExtensionMode == "youtube_music_preset",
                                        onClick = { mainViewModel.setSelectedExtensionMode("youtube_music_preset") },
                                        label = { Text("🎵 YouTube Music", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = com.example.ui.theme.SpotifyGreen,
                                            selectedLabelColor = Color.Black,
                                            containerColor = com.example.ui.theme.SpotifyCardBg,
                                            labelColor = com.example.ui.theme.SpotifyTextPrimary
                                        )
                                    )
                                }
                            }

                            // Songs Tab with filter results
                            SongsTab(
                                songs = songs,
                                currentPlayingSong = currentSong,
                                isPlaying = isPlaying,
                                isLoading = isLoading,
                                themeColor = themeColor,
                                onSongClick = { list, index -> playerViewModel.playSongList(list, index) },
                                onPlayPauseToggle = { playerViewModel.togglePlayPause() },
                                onToggleFavorite = { mainViewModel.toggleFavorite(it) },
                                onAddToPlaylist = { mainViewModel.openAddToPlaylistDialog(it) }
                            )
                        }
                    }

                    2 -> {
                        // 2: YOUR LIBRARY SCREEN
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.SpotifyGreen,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(end = 8.dp)
                                )

                                Text(
                                    text = "Your Library",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = com.example.ui.theme.SpotifyTextPrimary
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                SortMenu(
                                    currentSort = sortOrder,
                                    onSortSelected = { mainViewModel.setSortOrder(it) }
                                )
                            }

                            // Scrollable Category Tabs Row
                            ScrollableTabRow(
                                selectedTabIndex = selectedTab,
                                containerColor = com.example.ui.theme.SpotifyDarkCanvas,
                                contentColor = com.example.ui.theme.SpotifyTextPrimary,
                                edgePadding = 16.dp,
                                indicator = { tabPositions ->
                                    if (selectedTab < tabPositions.size) {
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                            color = com.example.ui.theme.SpotifyGreen,
                                            height = 3.dp
                                        )
                                    }
                                }
                            ) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        selected = selectedTab == index,
                                        onClick = { mainViewModel.setSelectedTab(index) },
                                        text = {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 15.sp
                                                ),
                                                color = if (selectedTab == index) com.example.ui.theme.SpotifyGreen else com.example.ui.theme.SpotifyTextSecondary
                                            )
                                        },
                                        modifier = Modifier.testTag("tab_$title")
                                    )
                                }
                            }

                            // Library Tab View
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                when (selectedTab) {
                                    0 -> SongsTab(
                                        songs = songs,
                                        currentPlayingSong = currentSong,
                                        isPlaying = isPlaying,
                                        isLoading = isLoading,
                                        themeColor = themeColor,
                                        onSongClick = { list, index -> playerViewModel.playSongList(list, index) },
                                        onPlayPauseToggle = { playerViewModel.togglePlayPause() },
                                        onToggleFavorite = { mainViewModel.toggleFavorite(it) },
                                        onAddToPlaylist = { mainViewModel.openAddToPlaylistDialog(it) }
                                    )
                                    1 -> AlbumsTab(
                                        albums = albums,
                                        allSongs = songs,
                                        themeColor = themeColor,
                                        onAlbumClick = { album, albumSongs ->
                                            mainViewModel.openMediaDetail(
                                                type = "ALBUM",
                                                title = album.title,
                                                subtitle = album.artist,
                                                artworkUri = album.albumArtUri,
                                                initialSongs = albumSongs
                                            )
                                        }
                                    )
                                    2 -> ArtistsTab(
                                        artists = artists,
                                        allSongs = songs,
                                        themeColor = themeColor,
                                        onArtistClick = { artist, artistSongs ->
                                            mainViewModel.openMediaDetail(
                                                type = "ARTIST",
                                                title = artist.name,
                                                subtitle = "${artistSongs.size} Songs",
                                                artworkUri = null,
                                                initialSongs = artistSongs
                                            )
                                        }
                                    )
                                    3 -> PlaylistsTab(
                                        playlists = playlists,
                                        allSongs = songs,
                                        playlistItems = playlistItems,
                                        themeColor = themeColor,
                                        onCreatePlaylistClick = { mainViewModel.openCreatePlaylistDialog() },
                                        onPlaylistClick = { playlistName, list ->
                                            mainViewModel.openMediaDetail(
                                                type = "PLAYLIST",
                                                title = playlistName,
                                                subtitle = "Playlist • ${list.size} tracks",
                                                artworkUri = null,
                                                initialSongs = list
                                            )
                                        },
                                        onDeletePlaylist = { mainViewModel.deletePlaylist(it) }
                                    )
                                    4 -> FoldersTab(
                                        folders = folders,
                                        allSongs = songs,
                                        themeColor = themeColor,
                                        onFolderClick = { folder, folderSongs ->
                                            mainViewModel.openMediaDetail(
                                                type = "FOLDER",
                                                title = folder.name,
                                                subtitle = folder.path,
                                                artworkUri = null,
                                                initialSongs = folderSongs
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // 3: PROFILE & SETTINGS TAB
                        ProfileTab(
                            songCount = songs.size,
                            pluginCount = plugins.size,
                            accounts = extensionAccounts,
                            installedPlugins = plugins,
                            selectedExtensionMode = selectedExtensionMode,
                            onSelectExtensionMode = { mode -> mainViewModel.setSelectedExtensionMode(mode) },
                            onSaveAccount = { extId, username, channel, token ->
                                mainViewModel.saveExtensionAccount(extId, username, channel, token)
                            },
                            onLogoutAccount = { extId ->
                                mainViewModel.logoutExtensionAccount(extId)
                            },
                            onOpenEqualizer = { playerViewModel.setShowEqualizerDialog(true) },
                            onOpenSleepTimer = { playerViewModel.setShowSleepTimerDialog(true) },
                            onScanMusic = { mainViewModel.refreshMusicLibrary() },
                            onOpenExtensions = {
                                bottomNavIndex = 2
                                mainViewModel.setSelectedTab(5)
                            }
                        )
                    }
                }
            }

            // Floating Mini-Player at bottom above navigation bar
            if (currentSong != null) {
                MiniPlayerBar(
                    song = currentSong,
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    themeColor = themeColor,
                    onBarClick = { playerViewModel.setNowPlayingExpanded(true) },
                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                    onNextClick = { playerViewModel.playNext() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Spotify Style Bottom Navigation Bar
            NavigationBar(
                containerColor = Color(0xFF121212),
                contentColor = com.example.ui.theme.SpotifyTextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spotify_bottom_navigation")
            ) {
                NavigationBarItem(
                    selected = bottomNavIndex == 0,
                    onClick = { bottomNavIndex = 0 },
                    icon = {
                        Icon(
                            imageVector = if (bottomNavIndex == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = if (bottomNavIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.SpotifyGreen,
                        selectedTextColor = com.example.ui.theme.SpotifyGreen,
                        unselectedIconColor = com.example.ui.theme.SpotifyTextSecondary,
                        unselectedTextColor = com.example.ui.theme.SpotifyTextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    selected = bottomNavIndex == 1,
                    onClick = { bottomNavIndex = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    label = { Text("Search", fontSize = 11.sp, fontWeight = if (bottomNavIndex == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.SpotifyGreen,
                        selectedTextColor = com.example.ui.theme.SpotifyGreen,
                        unselectedIconColor = com.example.ui.theme.SpotifyTextSecondary,
                        unselectedTextColor = com.example.ui.theme.SpotifyTextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    selected = bottomNavIndex == 2,
                    onClick = { bottomNavIndex = 2 },
                    icon = {
                        Icon(
                            imageVector = if (bottomNavIndex == 2) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                            contentDescription = "Your Library"
                        )
                    },
                    label = { Text("Your Library", fontSize = 11.sp, fontWeight = if (bottomNavIndex == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.SpotifyGreen,
                        selectedTextColor = com.example.ui.theme.SpotifyGreen,
                        unselectedIconColor = com.example.ui.theme.SpotifyTextSecondary,
                        unselectedTextColor = com.example.ui.theme.SpotifyTextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    selected = bottomNavIndex == 3,
                    onClick = { bottomNavIndex = 3 },
                    icon = {
                        Icon(
                            imageVector = if (bottomNavIndex == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = if (bottomNavIndex == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.SpotifyGreen,
                        selectedTextColor = com.example.ui.theme.SpotifyGreen,
                        unselectedIconColor = com.example.ui.theme.SpotifyTextSecondary,
                        unselectedTextColor = com.example.ui.theme.SpotifyTextSecondary,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }

        // Fullscreen Now Playing Overlay
        AnimatedVisibility(
            visible = isNowPlayingExpanded,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NowPlayingSheet(
                song = currentSong,
                isPlaying = isPlaying,
                isLoading = isLoading,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                repeatMode = repeatMode,
                isShuffle = isShuffle,
                sleepTimerMsLeft = sleepTimerMsLeft,
                allSongs = songs,
                activeThemeColor = themeColor,
                onThemeChange = { mainViewModel.setThemeColor(it) },
                onPlayPauseClick = { playerViewModel.togglePlayPause() },
                onNextClick = { playerViewModel.playNext() },
                onPreviousClick = { playerViewModel.playPrevious() },
                onSeek = { playerViewModel.seekTo(it) },
                onToggleRepeat = { playerViewModel.toggleRepeatMode() },
                onToggleShuffle = { playerViewModel.toggleShuffle() },
                onToggleFavorite = { mainViewModel.toggleFavorite(it) },
                onOpenEqualizer = { playerViewModel.setShowEqualizerDialog(true) },
                onOpenSleepTimer = { playerViewModel.setShowSleepTimerDialog(true) },
                onScanForSongs = { mainViewModel.refreshMusicLibrary() },
                onSelectSong = { targetSong ->
                    val index = songs.indexOfFirst { it.id == targetSong.id }
                    if (index >= 0) {
                        playerViewModel.playSongList(songs, index)
                    }
                },
                onDismiss = { playerViewModel.setNowPlayingExpanded(false) }
            )
        }

        // Equalizer Dialog
        if (showEqualizerDialog) {
            EqualizerDialog(
                info = equalizerInfo,
                onPresetSelected = { playerViewModel.setEqualizerPreset(it) },
                onBandLevelChanged = { band, level -> playerViewModel.setEqualizerBandLevel(band, level) },
                onBassBoostChanged = { playerViewModel.setBassBoost(it) },
                onVirtualizerChanged = { playerViewModel.setVirtualizer(it) },
                onDismiss = { playerViewModel.setShowEqualizerDialog(false) }
            )
        }

        // Sleep Timer Dialog
        if (showSleepTimerDialog) {
            SleepTimerDialog(
                activeTimerMs = sleepTimerMsLeft,
                onSetTimer = { mins, endTrack -> playerViewModel.setSleepTimer(mins, endTrack) },
                onCancelTimer = { playerViewModel.cancelSleepTimer() },
                onDismiss = { playerViewModel.setShowSleepTimerDialog(false) }
            )
        }

        // Create Playlist Dialog
        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onConfirm = { mainViewModel.createPlaylist(it) },
                onDismiss = { mainViewModel.closeCreatePlaylistDialog() }
            )
        }

        // Add to Playlist Dialog
        if (songToAddToPlaylist != null) {
            AddToPlaylistDialog(
                song = songToAddToPlaylist!!,
                playlists = playlists,
                onSelectPlaylist = { mainViewModel.addSongToPlaylist(it.id, songToAddToPlaylist!!) },
                onCreateNewPlaylist = {
                    mainViewModel.closeAddToPlaylistDialog()
                    mainViewModel.openCreatePlaylistDialog()
                },
                onDismiss = { mainViewModel.closeAddToPlaylistDialog() }
            )
        }

        // Media Detail Sheet (Album, Playlist, Artist, Folder)
        if (mediaDetailState != null) {
            val detail = mediaDetailState!!
            MediaDetailSheet(
                title = detail.title,
                subtitle = detail.subtitle,
                categoryType = detail.type,
                artworkUri = detail.artworkUri,
                songs = detail.songs,
                currentPlayingSongId = currentSong?.id,
                isPlaying = isPlaying,
                isLoadingOnline = detail.isFetchingOnline,
                themeColor = themeColor,
                onDismiss = { mainViewModel.closeMediaDetail() },
                onSongClick = { list, index -> playerViewModel.playSongList(list, index) },
                onPlayAllClick = { playerViewModel.playSongList(detail.songs, 0) },
                onShuffleAllClick = { playerViewModel.playSongList(detail.songs.shuffled(), 0) },
                onToggleFavorite = { mainViewModel.toggleFavorite(it) },
                onFetchOnlineTracks = { query -> mainViewModel.fetchOnlineTracksForDetail(query) }
            )
        }

        // Audio & Settings Dialog
        if (showAudioSettingsDialog) {
            val context = LocalContext.current
            AlertDialog(
                onDismissRequest = { showAudioSettingsDialog = false },
                title = {
                    Column {
                        Text(
                            text = "Audio & Settings",
                            color = com.example.ui.theme.SpotifyTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Equalizer, sleep timer, storage scan & preferences",
                            color = com.example.ui.theme.SpotifyTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                ProfileOptionRow(
                                    icon = Icons.Default.Equalizer,
                                    title = "Equalizer & Sound Effects",
                                    subtitle = "Customize bass boost and 5-band EQ",
                                    onClick = {
                                        showAudioSettingsDialog = false
                                        playerViewModel.setShowEqualizerDialog(true)
                                    }
                                )

                                HorizontalDivider(color = Color(0xFF282828))

                                ProfileOptionRow(
                                    icon = Icons.Default.Timer,
                                    title = "Sleep Timer",
                                    subtitle = "Auto pause audio after set duration",
                                    onClick = {
                                        showAudioSettingsDialog = false
                                        playerViewModel.setShowSleepTimerDialog(true)
                                    }
                                )

                                HorizontalDivider(color = Color(0xFF282828))

                                ProfileOptionRow(
                                    icon = Icons.Default.Refresh,
                                    title = "Rescan Music Storage",
                                    subtitle = "Refresh device MP3 files and metadata",
                                    onClick = {
                                        showAudioSettingsDialog = false
                                        mainViewModel.refreshMusicLibrary()
                                        Toast.makeText(context, "Rescanning local music storage...", Toast.LENGTH_SHORT).show()
                                    }
                                )

                                HorizontalDivider(color = Color(0xFF282828))

                                ProfileOptionRow(
                                    icon = Icons.Default.Extension,
                                    title = "YouTube Music Extension",
                                    subtitle = "Active YouTube Music stream search and player",
                                    onClick = {
                                        showAudioSettingsDialog = false
                                        showManageExtensionsDialogBySettings = true
                                    }
                                )

                                HorizontalDivider(color = Color(0xFF282828))

                                ProfileOptionRow(
                                    icon = Icons.Default.Info,
                                    title = "About Lyra Music Player",
                                    subtitle = "Version 1.0.0 • Spotify Edition",
                                    onClick = {
                                        Toast.makeText(context, "Lyra Music Player v1.0.0", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showAudioSettingsDialog = false }) {
                        Text("Close", color = com.example.ui.theme.SpotifyTextSecondary)
                    }
                },
                containerColor = Color(0xFF282828)
            )
        }

        // Manage Extensions Dialog opened from Audio & Settings
        if (showManageExtensionsDialogBySettings) {
            ManageExtensionsDialog(
                plugins = plugins,
                searchResults = searchResults,
                isSearching = isSearchingOnline,
                accounts = extensionAccounts,
                themeColor = themeColor,
                onDismiss = { showManageExtensionsDialogBySettings = false },
                onSearchQuery = { query, extId -> mainViewModel.searchOnlineSongs(query, extId) },
                onSaveAccount = { extId, username, channelId, token -> mainViewModel.saveExtensionAccount(extId, username, channelId, token) },
                onLogoutAccount = { extId -> mainViewModel.logoutExtensionAccount(extId) },
                onInstallFromUrl = { url, callback -> mainViewModel.installPluginFromUrl(url, callback) },
                onInstallFromCode = { code, name, callback -> mainViewModel.installPluginFromCode(code, name, callback) },
                onInstallFromLocalUri = { uri, fileName, callback -> mainViewModel.installPluginFromLocalUri(uri, fileName, callback) },
                onTogglePlugin = { pluginId -> mainViewModel.togglePlugin(pluginId) },
                onDeletePlugin = { pluginId -> mainViewModel.deletePlugin(pluginId) },
                onPlayOnlineSong = { song -> playerViewModel.playSongList(listOf(song), 0) }
            )
        }
    }
}
