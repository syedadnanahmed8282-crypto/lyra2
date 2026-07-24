package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.LyraApplication
import com.example.model.Album
import com.example.model.Artist
import com.example.model.Folder
import com.example.model.Song
import com.example.model.SortOrder
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.VibrantPurple
import com.example.data.extension.toSong
import com.example.data.db.entity.PlaylistEntity
import com.example.data.db.entity.PlaylistItemEntity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as LyraApplication).repository
    val extensionManager = (application as LyraApplication).extensionManager

    val installedPlugins = extensionManager.installedPlugins
    val onlineSearchResults = extensionManager.searchResults
    val isSearchingOnline = extensionManager.isSearching
    val extensionAccounts = extensionManager.accountsMap

    private val _selectedExtensionMode = MutableStateFlow<String>("ALL")
    val selectedExtensionMode: StateFlow<String> = _selectedExtensionMode.asStateFlow()

    private val _themeColor = MutableStateFlow(VibrantPurple)
    val themeColor: StateFlow<Color> = _themeColor.asStateFlow()

    fun setThemeColor(color: Color) {
        _themeColor.value = color
    }

    fun setSelectedExtensionMode(mode: String) {
        _selectedExtensionMode.value = mode
        if (mode != "LOCAL") {
            val query = searchQuery.value.ifBlank { "music" }
            viewModelScope.launch {
                extensionManager.searchOnlineSongs(query, mode)
            }
        }
    }

    val songs: StateFlow<List<Song>> = combine(
        repository.songsList,
        extensionManager.searchResults,
        _selectedExtensionMode
    ) { localSongs, onlineSongs, mode ->
        val cleanLocalSongs = localSongs.filter { !it.folderPath.startsWith("Online") }
        val onlineAsSongs = onlineSongs.map { it.toSong() }
        when (mode) {
            "LOCAL" -> cleanLocalSongs
            "ALL" -> (cleanLocalSongs + onlineAsSongs).distinctBy { it.id }
            else -> {
                // Return strictly matching songs for the selected extension ID or preset
                val matchingOnline = onlineSongs.filter { os ->
                    os.extensionId == mode || os.extensionName.equals(mode, ignoreCase = true)
                }.map { it.toSong() }
                matchingOnline
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val albums: StateFlow<List<Album>> = songs.map { songList ->
        songList.groupBy { song ->
            song.albumId.takeIf { id -> id != 0L } ?: song.album.ifBlank { "Unknown Album" }.hashCode().toLong()
        }.map { (albId, albumSongs) ->
            val first = albumSongs.first()
            Album(
                id = albId,
                title = first.album.ifBlank { "Unknown Album" },
                artist = first.artist.ifBlank { "Unknown Artist" },
                songCount = albumSongs.size,
                albumArtUri = first.albumArtUri,
                demoDrawableRes = first.demoDrawableRes
            )
        }.sortedBy { it.title.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val artists: StateFlow<List<Artist>> = songs.map { songList ->
        songList.groupBy { it.artist.ifBlank { "Unknown Artist" } }
            .map { (artistName, artistSongs) ->
                Artist(
                    name = artistName,
                    songCount = artistSongs.size,
                    albumCount = artistSongs.map { it.album }.distinct().size
                )
            }.sortedBy { it.name.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val folders: StateFlow<List<Folder>> = songs.map { songList ->
        songList.groupBy { song ->
            song.folderPath.ifBlank {
                if (song.folderName.isNotBlank()) song.folderName else "Local Music"
            }
        }.map { (fPath, folderSongs) ->
            val name = folderSongs.firstOrNull()?.folderName?.ifBlank { null }
                ?: fPath.substringAfterLast('/').ifBlank { "Music Folder" }
            Folder(
                name = name,
                path = fPath,
                songCount = folderSongs.size
            )
        }.sortedBy { it.name.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val playlists: StateFlow<List<PlaylistEntity>> = combine(
        repository.playlistsList,
        _selectedExtensionMode,
        songs,
        extensionAccounts
    ) { dbPlaylists, mode, songList, _ ->
        val onlineGrouped = songList.filter { it.folderPath.startsWith("Online") }
            .groupBy { it.folderName.ifBlank { "Account Playlist" } }
            .entries.toList()
            .mapIndexed { index, entry ->
                PlaylistEntity(
                    id = 800000L + index + (mode.hashCode().toLong() % 10000),
                    name = entry.key,
                    songCount = entry.value.size
                )
            }
        if (mode != "LOCAL" && mode != "ALL") {
            if (onlineGrouped.isNotEmpty()) onlineGrouped else dbPlaylists
        } else if (onlineGrouped.isNotEmpty()) {
            dbPlaylists + onlineGrouped
        } else {
            dbPlaylists
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val playlistItems: StateFlow<List<PlaylistItemEntity>> = repository.playlistItemsList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val searchQuery: StateFlow<String> = repository.searchQuery
    val sortOrder: StateFlow<SortOrder> = repository.sortOrder

    private val _selectedTab = MutableStateFlow(0) // 0: Songs, 1: Albums, 2: Artists, 3: Playlists, 4: Folders
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

    private val _songToAddToPlaylist = MutableStateFlow<Song?>(null)
    val songToAddToPlaylist: StateFlow<Song?> = _songToAddToPlaylist.asStateFlow()

    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

    init {
        refreshMusicLibrary()
    }

    fun setPermissionGranted(granted: Boolean) {
        _isPermissionGranted.value = granted
        if (granted) {
            refreshMusicLibrary()
        }
    }

    fun refreshMusicLibrary() {
        viewModelScope.launch {
            repository.refreshLocalAudio()
        }
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setSearchQuery(query: String) {
        repository.setSearchQuery(query)
        viewModelScope.launch {
            extensionManager.searchOnlineSongs(query, _selectedExtensionMode.value)
        }
    }

    fun setSortOrder(order: SortOrder) {
        repository.setSortOrder(order)
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            repository.toggleFavorite(song.id, song.isFavorite)
        }
    }

    fun openAddToPlaylistDialog(song: Song) {
        _songToAddToPlaylist.value = song
    }

    fun closeAddToPlaylistDialog() {
        _songToAddToPlaylist.value = null
    }

    fun openCreatePlaylistDialog() {
        _showCreatePlaylistDialog.value = true
    }

    fun closeCreatePlaylistDialog() {
        _showCreatePlaylistDialog.value = false
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createPlaylist(name.trim())
            closeCreatePlaylistDialog()
        }
    }

    fun addSongToPlaylist(playlistId: Long, song: Song) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, song.id)
            closeAddToPlaylistDialog()
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun searchOnlineSongs(query: String, selectedExtensionId: String? = null) {
        viewModelScope.launch {
            val extId = selectedExtensionId ?: _selectedExtensionMode.value
            extensionManager.searchOnlineSongs(query, extId)
        }
    }

    fun installPluginFromUrl(url: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = extensionManager.installPluginFromUrl(url)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Installation failed")
            }
        }
    }

    fun installPluginFromCode(code: String, name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = extensionManager.installPluginFromCode(code, name)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Failed to parse code")
            }
        }
    }

    fun installPluginFromLocalUri(uri: android.net.Uri, fileName: String?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = extensionManager.installPluginFromLocalUri(uri, fileName)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Failed to read local file")
            }
        }
    }

    fun togglePlugin(pluginId: String) {
        viewModelScope.launch {
            extensionManager.togglePlugin(pluginId)
        }
    }

    fun deletePlugin(pluginId: String) {
        viewModelScope.launch {
            extensionManager.deletePlugin(pluginId)
        }
    }

    fun saveExtensionAccount(extensionId: String, username: String, channelOrPlaylistId: String, authToken: String = "") {
        viewModelScope.launch {
            val account = com.example.data.extension.ExtensionAccount(
                extensionId = extensionId,
                username = username,
                channelOrPlaylistId = channelOrPlaylistId,
                authToken = authToken,
                isLoggedIn = true
            )
            extensionManager.saveAccount(account)
            extensionManager.searchOnlineSongs("", extensionId)
        }
    }

    fun logoutExtensionAccount(extensionId: String) {
        viewModelScope.launch {
            extensionManager.logoutAccount(extensionId)
            extensionManager.searchOnlineSongs("", extensionId)
        }
    }
}
