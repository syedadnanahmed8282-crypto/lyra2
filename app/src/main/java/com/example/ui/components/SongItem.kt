package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Song
import com.example.ui.theme.SpotifyCardHover
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyTextPrimary
import com.example.ui.theme.SpotifyTextSecondary

@Composable
fun SongItem(
    song: Song,
    isPlayingCurrent: Boolean,
    isPlaying: Boolean = true,
    isLoading: Boolean = false,
    themeColor: Color = SpotifyGreen,
    onSongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayPauseToggle: () -> Unit = onSongClick,
    onAddToPlaylist: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val titleColor = if (isPlayingCurrent) SpotifyGreen else SpotifyTextPrimary
    val itemBg = if (isPlayingCurrent) SpotifyCardHover else Color.Transparent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(itemBg)
            .clickable { onSongClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .testTag("song_item_${song.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail Image
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            if (song.demoDrawableRes != null) {
                Image(
                    painter = painterResource(id = song.demoDrawableRes),
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (song.albumArtUri != null) {
                AsyncImage(
                    model = song.albumArtUri,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if (isPlayingCurrent) SpotifyGreen else SpotifyTextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isPlayingCurrent) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = SpotifyGreen,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = SpotifyGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Song Title & Artist
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isPlayingCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${song.artist} • ${song.album}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                ),
                color = SpotifyTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Spotify Green Favorite Heart Button
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .size(36.dp)
                .testTag("favorite_button_${song.id}")
        ) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (song.isFavorite) SpotifyGreen else SpotifyTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Options / Add to Playlist
        IconButton(
            onClick = onAddToPlaylist,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = SpotifyTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
