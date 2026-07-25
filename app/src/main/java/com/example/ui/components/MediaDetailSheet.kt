package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.Song
import com.example.ui.theme.SpotifyCardBg
import com.example.ui.theme.SpotifyCardHover
import com.example.ui.theme.SpotifyDarkCanvas
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyTextPrimary
import com.example.ui.theme.SpotifyTextSecondary

@Composable
fun MediaDetailSheet(
    title: String,
    subtitle: String,
    categoryType: String = "PLAYLIST", // "ALBUM", "PLAYLIST", "ARTIST", "FOLDER"
    artworkUri: Uri? = null,
    songs: List<Song>,
    currentPlayingSongId: Long? = null,
    isPlaying: Boolean = false,
    isLoadingOnline: Boolean = false,
    themeColor: Color = SpotifyGreen,
    onDismiss: () -> Unit,
    onSongClick: (List<Song>, Int) -> Unit,
    onPlayAllClick: () -> Unit,
    onShuffleAllClick: () -> Unit,
    onToggleFavorite: (Song) -> Unit = {},
    onFetchOnlineTracks: ((String) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredSongs = remember(songs, searchQuery) {
        if (searchQuery.isBlank()) songs
        else songs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.artist.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp)
                .testTag("media_detail_sheet"),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = SpotifyDarkCanvas
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Category & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF282828), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = categoryType.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SpotifyGreen
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_sheet")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SpotifyTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Header Info Box (Spotify Dark Container)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SpotifyCardBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Artwork Image or Icon
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF282828)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (artworkUri != null) {
                                AsyncImage(
                                    model = artworkUri,
                                    contentDescription = title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = if (categoryType == "ALBUM") Icons.Default.Album else Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = SpotifyGreen,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SpotifyTextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subtitle,
                                fontSize = 13.sp,
                                color = SpotifyTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${songs.size} Tracks",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = SpotifyGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Spotify Action Row: Big Green Circular Play Button & Shuffle Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Big Spotify Green Play Button
                        Surface(
                            shape = CircleShape,
                            color = SpotifyGreen,
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .clickable(enabled = songs.isNotEmpty()) { onPlayAllClick() }
                                .testTag("detail_play_all_btn")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play All",
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        // Shuffle Button
                        IconButton(
                            onClick = { if (songs.isNotEmpty()) onShuffleAllClick() },
                            modifier = Modifier.testTag("detail_shuffle_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = SpotifyGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Text(
                        text = "${filteredSongs.size} song(s)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = SpotifyTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search inside detail
                if (songs.size > 5) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Filter tracks...", fontSize = 13.sp, color = SpotifyTextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SpotifyGreen) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpotifyGreen,
                            unfocusedBorderColor = Color(0xFF282828),
                            focusedContainerColor = SpotifyCardBg,
                            unfocusedContainerColor = SpotifyCardBg,
                            focusedTextColor = SpotifyTextPrimary,
                            unfocusedTextColor = SpotifyTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                HorizontalDivider(color = Color(0xFF282828))

                Spacer(modifier = Modifier.height(8.dp))

                // Song List
                if (isLoadingOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SpotifyGreen)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loading tracks for $title...", color = SpotifyTextSecondary, fontSize = 13.sp)
                        }
                    }
                } else if (filteredSongs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = SpotifyGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No tracks in this $categoryType", fontWeight = FontWeight.Bold, color = SpotifyTextPrimary)
                            Text("Search online streams for this title", fontSize = 12.sp, color = SpotifyTextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            if (onFetchOnlineTracks != null) {
                                Button(
                                    onClick = { onFetchOnlineTracks(title) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Fetch Online Tracks", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        itemsIndexed(filteredSongs, key = { _, song -> song.id }) { index, song ->
                            val isSelected = currentPlayingSongId == song.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { onSongClick(filteredSongs, index) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) SpotifyCardHover else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Track Index Number
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SpotifyGreen else SpotifyTextSecondary,
                                        modifier = Modifier.width(32.dp)
                                    )

                                    // Artwork
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF282828)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (song.albumArtUri != null) {
                                            AsyncImage(
                                                model = song.albumArtUri,
                                                contentDescription = song.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = if (isSelected) SpotifyGreen else SpotifyTextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = song.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) SpotifyGreen else SpotifyTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${song.artist} • ${song.album}",
                                            fontSize = 12.sp,
                                            color = SpotifyTextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = { onToggleFavorite(song) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (song.isFavorite) SpotifyGreen else SpotifyTextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
