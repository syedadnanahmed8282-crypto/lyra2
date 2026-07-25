package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.entity.PlaylistEntity
import com.example.data.db.entity.PlaylistItemEntity
import com.example.model.Song
import com.example.ui.theme.SpotifyCardBg
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyTextPrimary
import com.example.ui.theme.SpotifyTextSecondary

@Composable
fun PlaylistsTab(
    playlists: List<PlaylistEntity>,
    allSongs: List<Song>,
    playlistItems: List<PlaylistItemEntity> = emptyList(),
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (String, List<Song>) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    themeColor: Color = SpotifyGreen,
    modifier: Modifier = Modifier
) {
    val favoriteSongs = allSongs.filter { it.isFavorite }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("playlists_list"),
        contentPadding = PaddingValues(12.dp)
    ) {
        item {
            Button(
                onClick = onCreatePlaylistClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("create_playlist_btn"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Playlist",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Favorites Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPlaylistClick("Favorites", favoriteSongs) }
                    .testTag("favorites_playlist_card"),
                colors = CardDefaults.cardColors(containerColor = SpotifyCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF282828)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = SpotifyGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Liked Songs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = SpotifyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Playlist • ${favoriteSongs.size} songs",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = SpotifyTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SpotifyTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        items(playlists, key = { it.id }) { playlist ->
            val pSongIds = playlistItems.filter { it.playlistId == playlist.id }.map { it.songId }.toSet()
            val playlistSongs = if (pSongIds.isNotEmpty()) {
                allSongs.filter { pSongIds.contains(it.id) }
            } else {
                allSongs.filter {
                    it.folderName.equals(playlist.name, ignoreCase = true) ||
                            it.album.equals(playlist.name, ignoreCase = true) ||
                            it.artist.equals(playlist.name, ignoreCase = true) ||
                            it.title.contains(playlist.name, ignoreCase = true)
                }
            }
            val countDisplay = if (playlistSongs.isNotEmpty()) playlistSongs.size else playlist.songCount

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPlaylistClick(playlist.name, playlistSongs) }
                    .testTag("playlist_item_${playlist.id}"),
                colors = CardDefaults.cardColors(containerColor = SpotifyCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF282828)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playlist.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = SpotifyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Playlist • $countDisplay songs",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = SpotifyTextSecondary
                        )
                    }

                    IconButton(
                        onClick = { onDeletePlaylist(playlist.id) },
                        modifier = Modifier.testTag("delete_playlist_${playlist.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Playlist",
                            tint = SpotifyTextSecondary
                        )
                    }
                }
            }
        }
    }
}
