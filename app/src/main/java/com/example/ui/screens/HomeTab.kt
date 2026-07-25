package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Album
import com.example.model.Artist
import com.example.model.Song
import com.example.ui.theme.SpotifyCardBg
import com.example.ui.theme.SpotifyDarkCanvas
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyTextPrimary
import com.example.ui.theme.SpotifyTextSecondary
import java.util.Calendar

@Composable
fun HomeTab(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    onSongClick: (List<Song>, Int) -> Unit,
    onArtistClick: (Artist, List<Song>) -> Unit,
    onAlbumClick: (Album, List<Song>) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Music") }

    // Calculate dynamic greeting
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = remember(hour) {
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..22 -> "Good evening"
            else -> "Good night"
        }
    }

    val recentSongs = remember(songs) { songs.take(6) }
    val favoriteArtists = remember(artists) { artists.take(8) }
    val madeForYouSongs = remember(songs) { songs.take(10) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyDarkCanvas)
            .testTag("home_tab_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. Top Greeting Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = SpotifyTextPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = SpotifyTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = { /* History */ }) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "History",
                            tint = SpotifyTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SpotifyTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 2. Category Filter Pills
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Music", "Podcasts", "Audiobooks", "Favorites")
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SpotifyGreen,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF282828),
                            labelColor = SpotifyTextPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Section: Your favorite artist (Circular Avatar Horizontal Scroll)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Your favorite artist",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpotifyTextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (favoriteArtists.isEmpty()) {
                    // Fallback Demo Favorite Artists
                    val demoArtists = listOf("Nadeem Sarwar", "Ali Jee", "Ali Shanawar", "Farhan Ali Waris", "Aatif Aslam")
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(demoArtists) { artistName ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(80.dp)
                                    .clickable {
                                        val matches = songs.filter { it.artist.contains(artistName, ignoreCase = true) }
                                        if (matches.isNotEmpty()) onSongClick(matches, 0)
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF282828)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = artistName,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = artistName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SpotifyTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(favoriteArtists) { artist ->
                            val artistSongs = songs.filter { it.artist.equals(artist.name, ignoreCase = true) }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(84.dp)
                                    .clickable { onArtistClick(artist, artistSongs) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF282828)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = artist.name,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = artist.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SpotifyTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Section: Recent played (Quick Grid Row Cards - 2 Columns x 3 Rows)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent played",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotifyTextPrimary
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "More",
                        tint = SpotifyTextSecondary
                    )
                }

                val displayGridItems = if (recentSongs.isEmpty()) {
                    listOf(
                        "Nadeem Sarwar Mix",
                        "Abbas sa Kahna h...",
                        "Mola Karbala bula",
                        "Ali Jee Top Hits",
                        "Studio Recital",
                        "Favorite Tunes"
                    )
                } else {
                    recentSongs.map { it.title }
                }

                // 2-column layout for Recent Played
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    displayGridItems.chunked(2).forEach { rowPair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowPair.forEachIndexed { idx, titleText ->
                                val song = recentSongs.getOrNull(displayGridItems.indexOf(titleText))
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            if (song != null) {
                                                onSongClick(recentSongs, recentSongs.indexOf(song))
                                            } else if (songs.isNotEmpty()) {
                                                onSongClick(songs, 0)
                                            }
                                        },
                                    colors = CardDefaults.cardColors(containerColor = SpotifyCardBg),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .background(Color(0xFF282828)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (song?.demoDrawableRes != null) {
                                                Image(
                                                    painter = painterResource(id = song.demoDrawableRes),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = null,
                                                    tint = SpotifyGreen,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = titleText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SpotifyTextPrimary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .padding(horizontal = 10.dp)
                                                .weight(1f)
                                        )
                                    }
                                }
                            }

                            if (rowPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 5. Section: Made for you (Horizontal Scroll Cards with Big Artwork & Subtitle)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Made for you",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpotifyTextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (madeForYouSongs.isEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(listOf("Abbas sa Kahna hai" to "Ali jee", "Mola Karbala bula" to "Ali Shanawar", "Ya Ali Madad" to "Nadeem Sarwar")) { (title, artist) ->
                            Column(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpotifyCardBg)
                                    .padding(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF282828)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SpotifyTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = artist,
                                    fontSize = 12.sp,
                                    color = SpotifyTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(madeForYouSongs) { song ->
                            val songIdx = songs.indexOf(song)
                            Column(
                                modifier = Modifier
                                    .width(144.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpotifyCardBg)
                                    .clickable { onSongClick(songs, if (songIdx >= 0) songIdx else 0) }
                                    .padding(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF282828)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (song.demoDrawableRes != null) {
                                        Image(
                                            painter = painterResource(id = song.demoDrawableRes),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else if (song.albumArtUri != null) {
                                        AsyncImage(
                                            model = song.albumArtUri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = SpotifyGreen,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }

                                    // Floating Play Circle
                                    Surface(
                                        shape = CircleShape,
                                        color = SpotifyGreen,
                                        shadowElevation = 6.dp,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(6.dp)
                                            .size(34.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play",
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = song.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SpotifyTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = song.artist,
                                    fontSize = 12.sp,
                                    color = SpotifyTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 6. Section: Top Albums
        if (albums.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Popular Albums",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotifyTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(albums) { album ->
                            val albumSongs = songs.filter { it.album.equals(album.title, ignoreCase = true) }
                            Column(
                                modifier = Modifier
                                    .width(144.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpotifyCardBg)
                                    .clickable { onAlbumClick(album, albumSongs) }
                                    .padding(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF282828)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (album.demoDrawableRes != null) {
                                        Image(
                                            painter = painterResource(id = album.demoDrawableRes),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Album,
                                            contentDescription = null,
                                            tint = SpotifyGreen,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = album.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SpotifyTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${album.artist} • Album",
                                    fontSize = 12.sp,
                                    color = SpotifyTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
