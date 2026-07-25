package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.PaddingValues
import com.example.data.extension.ExtensionAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.extension.ExtensionPlugin
import com.example.data.extension.OnlineSong
import com.example.model.Song
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassCard
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name
}

fun OnlineSong.toSong(): Song {
    val numericId = id.hashCode().toLong().let { if (it < 0) -it else it }
    val resolvedFolder = if (album.isNotBlank() && album != "Unknown Album") album else "Online ($extensionName)"
    return Song(
        id = numericId,
        title = title,
        artist = artist,
        album = album,
        albumId = extensionId.hashCode().toLong(),
        duration = durationMs,
        path = streamUrl,
        contentUri = Uri.parse(streamUrl),
        albumArtUri = if (artworkUrl.isNotBlank()) Uri.parse(artworkUrl) else null,
        folderName = resolvedFolder,
        folderPath = "Online Extensions"
    )
}

@Composable
fun ExtensionsTab(
    plugins: List<ExtensionPlugin>,
    searchResults: List<OnlineSong>,
    isSearching: Boolean,
    accounts: Map<String, ExtensionAccount> = emptyMap(),
    themeColor: Color,
    onSearchQuery: (String, String?) -> Unit,
    onSaveAccount: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onLogoutAccount: (String) -> Unit = {},
    onInstallFromUrl: (String, (Boolean, String?) -> Unit) -> Unit = { _, _ -> },
    onInstallFromCode: (String, String, (Boolean, String?) -> Unit) -> Unit = { _, _, _ -> },
    onInstallFromLocalUri: (Uri, String?, (Boolean, String?) -> Unit) -> Unit = { _, _, _ -> },
    onTogglePlugin: (String) -> Unit = {},
    onDeletePlugin: (String) -> Unit = {},
    onPlayOnlineSong: (Song) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Auto-search initially
    LaunchedEffect(Unit) {
        if (searchResults.isEmpty()) {
            onSearchQuery("", "youtube_music_preset")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("extensions_tab")
    ) {
        SearchStreamsView(
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearchClick = { onSearchQuery(searchQuery, "youtube_music_preset") },
            searchResults = searchResults,
            isSearching = isSearching,
            themeColor = themeColor,
            onPlaySong = onPlayOnlineSong
        )
    }
}

@Composable
private fun SearchStreamsView(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    searchResults: List<OnlineSong>,
    isSearching: Boolean,
    themeColor: Color,
    onPlaySong: (Song) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text("Search YouTube Music streams...", fontSize = 14.sp, color = TextDarkSecondary) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("online_search_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = FrostedGlassBorder,
                    focusedContainerColor = FrostedGlassCard,
                    unfocusedContainerColor = FrostedGlassCard
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onSearchClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                modifier = Modifier.height(50.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        // Active extension indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎵 Powered by YouTube Music Extension",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )
            if (isSearching) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = themeColor,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fetching streams...", fontSize = 12.sp, color = themeColor)
                }
            }
        }

        if (searchResults.isEmpty() && !isSearching) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = themeColor.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No YouTube Music streams found", fontWeight = FontWeight.Medium, color = TextDarkSecondary)
                    Text("Type a song name or artist above to search and play", fontSize = 12.sp, color = TextDarkSecondary)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(searchResults) { onlineSong ->
                    OnlineSongItem(
                        onlineSong = onlineSong,
                        themeColor = themeColor,
                        onPlayClick = { onPlaySong(onlineSong.toSong()) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnlineSongItem(
    onlineSong: OnlineSong,
    themeColor: Color,
    onPlayClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(16.dp),
        color = FrostedGlassCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, FrostedGlassBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artwork
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(themeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (onlineSong.artworkUrl.isNotBlank()) {
                    AsyncImage(
                        model = onlineSong.artworkUrl,
                        contentDescription = onlineSong.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = onlineSong.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${onlineSong.artist} • ${onlineSong.album}",
                    fontSize = 13.sp,
                    color = TextDarkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .background(themeColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = onlineSong.extensionName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onPlayClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(themeColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun InstalledPluginsView(
    plugins: List<ExtensionPlugin>,
    accounts: Map<String, ExtensionAccount> = emptyMap(),
    themeColor: Color,
    onPickLocalFile: () -> Unit,
    onInstallClick: () -> Unit,
    onOpenAccountLogin: () -> Unit = {},
    onTogglePlugin: (String) -> Unit,
    onDeletePlugin: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Extension Account Login Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .clickable { onOpenAccountLogin() },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Account",
                    tint = themeColor,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Platform Account Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Log in with Gmail & Password to sync saved playlists, uploaded music, private folders & suggestions",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onOpenAccountLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Accounts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Install action buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onPickLocalFile,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pick_file_extension_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pick .eapk / .js File", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onInstallClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("install_extension_dialog_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColor)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("URL / Code", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (plugins.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No plugins installed", color = TextDarkSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(plugins) { plugin ->
                    PluginCard(
                        plugin = plugin,
                        themeColor = themeColor,
                        onToggle = { onTogglePlugin(plugin.id) },
                        onDelete = { onDeletePlugin(plugin.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PluginCard(
    plugin: ExtensionPlugin,
    themeColor: Color,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = FrostedGlassCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, FrostedGlassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Extension,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plugin.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "v${plugin.version}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Text(
                        text = "Author: ${plugin.author}",
                        fontSize = 12.sp,
                        color = TextDarkSecondary
                    )
                }

                Switch(
                    checked = plugin.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(checkedThumbColor = themeColor, checkedTrackColor = themeColor.copy(alpha = 0.4f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = plugin.description,
                fontSize = 13.sp,
                color = TextDarkPrimary.copy(alpha = 0.8f)
            )

            if (!plugin.sourceUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextDarkSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = plugin.sourceUrl,
                        fontSize = 11.sp,
                        color = TextDarkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Plugin", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun InstallPluginDialog(
    themeColor: Color,
    onDismiss: () -> Unit,
    onPickLocalFile: () -> Unit,
    onInstallUrl: (String, (Boolean, String?) -> Unit) -> Unit,
    onInstallCode: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var mode by remember { mutableStateOf(0) } // 0: Local File, 1: URL, 2: Code
    var urlInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var pluginNameInput by remember { mutableStateOf("Custom Plugin") }
    var isInstalling by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isInstalling) onDismiss() },
        title = {
            Text("Install Music Extension", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onPickLocalFile,
                        enabled = !isInstalling,
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = themeColor.copy(alpha = 0.1f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("File", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedButton(
                        onClick = { mode = 1 },
                        enabled = !isInstalling,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (mode == 1) themeColor.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("URL", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedButton(
                        onClick = { mode = 2 },
                        enabled = !isInstalling,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (mode == 2) themeColor.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("JS Code", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isInstalling) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = themeColor, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Downloading & installing extension...", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                } else if (mode == 1) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Plugin Script / Shortcode URL (.js / .eapk)") },
                        placeholder = { Text("https://example.com/extension.eapk") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (mode == 2) {
                    OutlinedTextField(
                        value = pluginNameInput,
                        onValueChange = { pluginNameInput = it },
                        label = { Text("Plugin Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it },
                        label = { Text("JavaScript Extension Code") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("function search(query) { return JSON.stringify([...]); }") }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(themeColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .clickable(enabled = !isInstalling) { onPickLocalFile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = themeColor, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Click to select .eapk or .js file", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (mode == 0) {
                Button(
                    onClick = onPickLocalFile,
                    enabled = !isInstalling,
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text("Select File")
                }
            } else {
                Button(
                    onClick = {
                        if (mode == 1) {
                            if (urlInput.isNotBlank()) {
                                isInstalling = true
                                onInstallUrl(urlInput.trim()) { _, _ ->
                                    isInstalling = false
                                }
                            }
                        } else {
                            if (codeInput.isNotBlank()) {
                                isInstalling = true
                                onInstallCode(codeInput.trim(), pluginNameInput.trim()) { _, _ ->
                                    isInstalling = false
                                }
                            }
                        }
                    },
                    enabled = !isInstalling && ((mode == 1 && urlInput.isNotBlank()) || (mode == 2 && codeInput.isNotBlank())),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text(if (isInstalling) "Installing..." else "Install")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isInstalling) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExtensionAccountDialog(
    plugins: List<ExtensionPlugin>,
    currentAccounts: Map<String, ExtensionAccount>,
    initialSelectedExtId: String?,
    themeColor: Color,
    onDismiss: () -> Unit,
    onSaveAccount: (String, String, String, String) -> Unit,
    onLogoutAccount: (String) -> Unit
) {
    val presets = listOf(
        Pair("youtube_music_preset", "YouTube Music"),
        Pair("spotify_music_preset", "Spotify Music"),
        Pair("itunes_music_preset", "iTunes / Apple Music"),
        Pair("jamendo_music_preset", "Jamendo Open Audio"),
        Pair("sound_stream_preset", "Echo Streamer")
    ) + plugins.map { Pair(it.id, it.name) }

    var selectedExtId by remember { mutableStateOf(initialSelectedExtId ?: presets.first().first) }
    val existingAcc = currentAccounts[selectedExtId]

    var emailInput by remember { mutableStateOf(existingAcc?.username ?: "") }
    var passwordInput by remember { mutableStateOf(existingAcc?.channelOrPlaylistId ?: "") }

    LaunchedEffect(selectedExtId) {
        val acc = currentAccounts[selectedExtId]
        emailInput = acc?.username ?: ""
        passwordInput = acc?.channelOrPlaylistId ?: ""
    }

    val platformName = when (selectedExtId) {
        "youtube_music_preset" -> "YouTube"
        "spotify_music_preset" -> "Spotify"
        "itunes_music_preset" -> "iTunes / Apple Music"
        "jamendo_music_preset" -> "Jamendo"
        else -> plugins.find { it.id == selectedExtId }?.name ?: "Platform"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = themeColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("$platformName Account Login", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column {
                Text(
                    "Enter your $platformName Gmail / Email and Password to log in. Your saved playlists, uploaded music, private folders, and personalized song suggestions will automatically sync into Lyra Music.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Select Platform:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    items(presets) { (id, name) ->
                        val isSel = selectedExtId == id
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedExtId = id },
                            label = { Text(name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = themeColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Gmail / Email Address") },
                    placeholder = { Text("e.g. user@gmail.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    placeholder = { Text("••••••••") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (emailInput.isNotBlank()) {
                        onSaveAccount(selectedExtId, emailInput.trim(), passwordInput.trim(), "")
                        onDismiss()
                    }
                },
                enabled = emailInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Text("Login Account")
            }
        },
        dismissButton = {
            Row {
                if (existingAcc != null && existingAcc.isLoggedIn) {
                    TextButton(onClick = {
                        onLogoutAccount(selectedExtId)
                        onDismiss()
                    }) {
                        Text("Log Out", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun ManageExtensionsDialog(
    plugins: List<ExtensionPlugin>,
    searchResults: List<OnlineSong>,
    isSearching: Boolean,
    accounts: Map<String, ExtensionAccount> = emptyMap(),
    themeColor: Color,
    onDismiss: () -> Unit,
    onSearchQuery: (String, String?) -> Unit,
    onSaveAccount: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onLogoutAccount: (String) -> Unit = {},
    onInstallFromUrl: (String, (Boolean, String?) -> Unit) -> Unit,
    onInstallFromCode: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onInstallFromLocalUri: (Uri, String?, (Boolean, String?) -> Unit) -> Unit,
    onTogglePlugin: (String) -> Unit,
    onDeletePlugin: (String) -> Unit,
    onPlayOnlineSong: (Song) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp),
            color = com.example.ui.theme.SpotifyDarkCanvas
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = "Extensions",
                            tint = themeColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Extensions & Plugin Sources",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = com.example.ui.theme.SpotifyTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = com.example.ui.theme.SpotifyTextPrimary
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF282828))

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    ExtensionsTab(
                        plugins = plugins,
                        searchResults = searchResults,
                        isSearching = isSearching,
                        accounts = accounts,
                        themeColor = themeColor,
                        onSearchQuery = onSearchQuery,
                        onSaveAccount = onSaveAccount,
                        onLogoutAccount = onLogoutAccount,
                        onInstallFromUrl = onInstallFromUrl,
                        onInstallFromCode = onInstallFromCode,
                        onInstallFromLocalUri = onInstallFromLocalUri,
                        onTogglePlugin = onTogglePlugin,
                        onDeletePlugin = onDeletePlugin,
                        onPlayOnlineSong = onPlayOnlineSong
                    )
                }
            }
        }
    }
}

