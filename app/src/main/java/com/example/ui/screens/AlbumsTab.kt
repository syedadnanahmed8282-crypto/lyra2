package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Album
import com.example.model.Song
import com.example.ui.components.AlbumCard
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyTextPrimary
import com.example.ui.theme.SpotifyTextSecondary

@Composable
fun AlbumsTab(
    albums: List<Album>,
    allSongs: List<Song>,
    onAlbumClick: (Album, List<Song>) -> Unit,
    themeColor: Color = SpotifyGreen,
    modifier: Modifier = Modifier
) {
    if (albums.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    tint = SpotifyGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Albums Found",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SpotifyTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No audio albums available in device media storage.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SpotifyTextSecondary
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .testTag("albums_grid"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(albums, key = { it.id }) { album ->
                val albumSongs = allSongs.filter { 
                    it.albumId == album.id || 
                    (it.album.isNotBlank() && it.album.equals(album.title, ignoreCase = true))
                }
                AlbumCard(
                    album = album,
                    onAlbumClick = { onAlbumClick(album, albumSongs) },
                    themeColor = SpotifyGreen
                )
            }
        }
    }
}
