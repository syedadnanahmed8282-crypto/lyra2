package com.example.repository

import android.content.Context
import com.example.data.db.dao.FavoriteDao
import com.example.data.db.dao.PlaylistDao
import com.example.data.db.entity.FavoriteEntity
import com.example.data.db.entity.PlaylistEntity
import com.example.data.db.entity.PlaylistItemEntity
import com.example.data.scanner.MediaStoreScanner
import com.example.model.Album
import com.example.model.Artist
import com.example.model.Folder
import com.example.model.Song
import com.example.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MusicRepository(
    private val context: Context,
    private val favoriteDao: FavoriteDao,
    private val playlistDao: PlaylistDao
) {
    private val scanner = MediaStoreScanner(context)

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.TITLE_ASC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val playlistsList: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()
    val playlistItemsList: Flow<List<PlaylistItemEntity>> = playlistDao.getAllPlaylistItems()

    private val favoritesFlow: Flow<Set<Long>> = favoriteDao.getAllFavorites().map { list ->
        list.map { it.songId }.toSet()
    }

    val songsList: Flow<List<Song>> = combine(
        _allSongs,
        favoritesFlow,
        _searchQuery,
        _sortOrder
    ) { songs, favIds, query, sort ->
        val updatedSongs = songs.map { song ->
            song.copy(isFavorite = favIds.contains(song.id))
        }

        val filteredSongs = if (query.isBlank()) {
            updatedSongs
        } else {
            val q = query.lowercase().trim()
            updatedSongs.filter { song ->
                val title = song.title.lowercase()
                val artist = song.artist.lowercase()
                val album = song.album.lowercase()
                title.contains(q) || artist.contains(q) || album.contains(q) ||
                        isFuzzyMatch(title, q) || isFuzzyMatch(artist, q)
            }
        }

        when (sort) {
            SortOrder.TITLE_ASC -> filteredSongs.sortedBy { it.title.lowercase() }
            SortOrder.TITLE_DESC -> filteredSongs.sortedByDescending { it.title.lowercase() }
            SortOrder.DATE_ADDED_DESC -> filteredSongs.sortedByDescending { it.dateAdded }
            SortOrder.DURATION_DESC -> filteredSongs.sortedByDescending { it.duration }
            SortOrder.SIZE_DESC -> filteredSongs.sortedByDescending { it.size }
        }
    }

    val albumsList: Flow<List<Album>> = songsList.map { songs ->
        songs.groupBy { it.albumId.takeIf { id -> id != 0L } ?: it.album.hashCode().toLong() }
            .map { (_, albumSongs) ->
                val first = albumSongs.first()
                Album(
                    id = first.albumId.takeIf { id -> id != 0L } ?: first.album.hashCode().toLong(),
                    title = first.album,
                    artist = first.artist,
                    songCount = albumSongs.size,
                    albumArtUri = first.albumArtUri,
                    demoDrawableRes = first.demoDrawableRes
                )
            }.sortedBy { it.title.lowercase() }
    }

    val artistsList: Flow<List<Artist>> = songsList.map { songs ->
        songs.groupBy { it.artist }
            .map { (artistName, artistSongs) ->
                Artist(
                    name = artistName,
                    songCount = artistSongs.size,
                    albumCount = artistSongs.map { it.album }.distinct().size
                )
            }.sortedBy { it.name.lowercase() }
    }

    val foldersList: Flow<List<Folder>> = songsList.map { songs ->
        songs.groupBy { it.folderPath }
            .map { (folderPath, folderSongs) ->
                val folderName = folderSongs.firstOrNull()?.folderName ?: "Unknown Folder"
                Folder(
                    name = folderName,
                    path = folderPath,
                    songCount = folderSongs.size
                )
            }.sortedBy { it.name.lowercase() }
    }

    suspend fun refreshLocalAudio() {
        withContext(Dispatchers.IO) {
            val scanned = scanner.scanLocalAudioFiles()
            _allSongs.value = scanned
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    suspend fun toggleFavorite(songId: Long, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.deleteFavorite(songId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(songId = songId))
        }
    }

    suspend fun createPlaylist(name: String) {
        playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        playlistDao.addSongToPlaylist(playlistId, songId)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    private fun isFuzzyMatch(text: String, query: String): Boolean {
        val qWords = query.lowercase().split(" ").filter { it.isNotBlank() }
        val tWords = text.lowercase().split(" ").filter { it.isNotBlank() }
        if (qWords.isEmpty()) return true
        var matches = 0
        for (qw in qWords) {
            if (tWords.any { tw -> tw.contains(qw) || levenshteinDistance(tw, qw) <= (qw.length / 4).coerceAtLeast(1) }) {
                matches++
            }
        }
        return matches == qWords.size
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        if (s1 == s2) return 0
        if (s1.isEmpty()) return s2.length
        if (s2.isEmpty()) return s1.length
        val dp = IntArray(s2.length + 1) { it }
        for (i in 1..s1.length) {
            var prev = dp[0]
            dp[0] = i
            for (j in 1..s2.length) {
                val temp = dp[j]
                if (s1[i - 1] == s2[j - 1]) {
                    dp[j] = prev
                } else {
                    dp[j] = 1 + minOf(prev, dp[j], dp[j - 1])
                }
                prev = temp
            }
        }
        return dp[s2.length]
    }
}
