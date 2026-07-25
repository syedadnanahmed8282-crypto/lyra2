package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.model.Album
import com.example.ui.theme.SpotifyCardBg
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyTextPrimary
import com.example.ui.theme.SpotifyTextSecondary

@Composable
fun AlbumCard(
    album: Album,
    onAlbumClick: () -> Unit,
    themeColor: Color = SpotifyGreen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onAlbumClick() }
            .testTag("album_card_${album.id}"),
        colors = CardDefaults.cardColors(containerColor = SpotifyCardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF282828)),
                contentAlignment = Alignment.Center
            ) {
                if (album.demoDrawableRes != null) {
                    Image(
                        painter = painterResource(id = album.demoDrawableRes),
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (album.albumArtUri != null) {
                    AsyncImage(
                        model = album.albumArtUri,
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        tint = SpotifyGreen,
                        modifier = Modifier.fillMaxSize(0.45f)
                    )
                }

                // Spotify Floating Green Play Button Overlay
                Surface(
                    shape = CircleShape,
                    color = SpotifyGreen,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(38.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = album.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = SpotifyTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${album.artist} • ${album.songCount} songs",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = SpotifyTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
