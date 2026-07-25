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
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
fun MiniPlayerBar(
    song: Song?,
    isPlaying: Boolean,
    isLoading: Boolean = false,
    currentPositionMs: Long,
    durationMs: Long,
    themeColor: Color = SpotifyGreen,
    onBarClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onBarClick() }
            .testTag("mini_player_bar"),
        colors = CardDefaults.cardColors(
            containerColor = SpotifyCardHover
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            // Linear Progress Line (Spotify Green)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = SpotifyGreen,
                trackColor = Color(0xFF3E3E3E)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF121212)),
                    contentAlignment = Alignment.Center
                ) {
                    if (song.demoDrawableRes != null) {
                        Image(
                            painter = painterResource(id = song.demoDrawableRes),
                            contentDescription = "Album Art",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (song.albumArtUri != null) {
                        AsyncImage(
                            model = song.albumArtUri,
                            contentDescription = "Album Art",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = SpotifyGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Track Title & Artist
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = SpotifyTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = SpotifyTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Controls: Play/Pause and Next
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.testTag("mini_play_pause")
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
                            tint = SpotifyTextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.testTag("mini_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = SpotifyTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

