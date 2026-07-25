package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.extension.ExtensionAccount
import com.example.data.extension.ExtensionPlugin
import com.example.ui.theme.SpotifyCardBg
import com.example.ui.theme.SpotifyDarkCanvas
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SpotifyTextPrimary
import com.example.ui.theme.SpotifyTextSecondary

private data class ExtensionItemUI(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun ProfileTab(
    userEmail: String = "ahmedrasel2k25@gmail.com",
    songCount: Int = 0,
    pluginCount: Int = 0,
    accounts: Map<String, ExtensionAccount> = emptyMap(),
    installedPlugins: List<ExtensionPlugin> = emptyList(),
    selectedExtensionMode: String = "ALL",
    onSelectExtensionMode: (String) -> Unit = {},
    onSaveAccount: (extensionId: String, username: String, channelOrPlaylistId: String, authToken: String) -> Unit = { _, _, _, _ -> },
    onLogoutAccount: (extensionId: String) -> Unit = {},
    onOpenEqualizer: () -> Unit = {},
    onOpenSleepTimer: () -> Unit = {},
    onScanMusic: () -> Unit = {},
    onOpenExtensions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoggedIn by remember { mutableStateOf(true) }
    var currentEmail by remember { mutableStateOf(userEmail) }
    var currentUserName by remember { mutableStateOf("Music Lover") }
    var showLoginDialog by remember { mutableStateOf(false) }

    val loggedInPlatforms = accounts.values.filter { it.isLoggedIn }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyDarkCanvas)
            .testTag("profile_tab_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = SpotifyCardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SpotifyGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isLoggedIn) currentUserName else "Guest User",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = SpotifyTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isLoggedIn) currentEmail else "Not logged in",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = SpotifyTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (loggedInPlatforms.isNotEmpty()) {
                                    "${loggedInPlatforms.size} accounts connected (YouTube/Spotify/iTunes)"
                                } else {
                                    "$songCount local tracks • Premium Member"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = SpotifyGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showLoginDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLoggedIn) Color(0xFF3E3E3E) else SpotifyGreen,
                            contentColor = if (isLoggedIn) SpotifyTextPrimary else Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isLoggedIn) Icons.Default.Person else Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isLoggedIn) "Edit Profile / Account" else "Log In / Sign Up",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Extension Selection Section Header
        item {
            Column(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
                Text(
                    text = "Extensions & Active Source",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpotifyTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Select an active extension source below. Selection is saved across app restarts.",
                    fontSize = 12.sp,
                    color = SpotifyTextSecondary
                )
            }
        }

        // List of Default and Installed Extensions
        item {
            val defaultExtensions = listOf(
                ExtensionItemUI("ALL", "All Extensions & Storage", "Combined search across local files & online extensions", Icons.Default.Cloud),
                ExtensionItemUI("LOCAL", "Local Device Storage", "Songs and MP3 audio stored on your phone storage", Icons.Default.Smartphone),
                ExtensionItemUI("youtube_music_preset", "YouTube Music Extension", "Playlists, uploaded music, videos & suggestions", Icons.Default.Extension),
                ExtensionItemUI("spotify_music_preset", "Spotify Music Extension", "Spotify library, top tracks & recommendations", Icons.Default.Extension),
                ExtensionItemUI("itunes_music_preset", "iTunes Apple Music Extension", "Apple Music top charts & album previews", Icons.Default.Extension),
                ExtensionItemUI("ncs_official_preset", "NoCopyrightSounds (NCS)", "Free copyright-free EDM and electronic tracks", Icons.Default.MusicNote),
                ExtensionItemUI("radio_browser_preset", "Radio Browser Extension", "Live streaming radio stations around the world", Icons.Default.Radio)
            )

            val extraPluginItems = installedPlugins.filter { plugin ->
                defaultExtensions.none { it.id == plugin.id }
            }.map { plugin ->
                ExtensionItemUI(
                    id = plugin.id,
                    name = plugin.name,
                    description = "${plugin.author} • v${plugin.version}",
                    icon = Icons.Default.Extension
                )
            }

            val allExtensionItems = defaultExtensions + extraPluginItems

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                allExtensionItems.forEach { ext ->
                    val isSelected = selectedExtensionMode == ext.id || selectedExtensionMode.equals(ext.name, ignoreCase = true)
                    val connectedAccount = accounts[ext.id]?.takeIf { it.isLoggedIn }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectExtensionMode(ext.id)
                                Toast.makeText(context, "${ext.name} selected as active source", Toast.LENGTH_SHORT).show()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF1E2A22) else SpotifyCardBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) BorderStroke(1.5.dp, SpotifyGreen) else BorderStroke(1.dp, Color(0xFF282828))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SpotifyGreen else Color(0xFF2A2A2A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = ext.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else SpotifyGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ext.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) SpotifyGreen else SpotifyTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = ext.description,
                                    fontSize = 11.sp,
                                    color = SpotifyTextSecondary
                                )
                                if (connectedAccount != null) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "✓ Account connected (${connectedAccount.username})",
                                        fontSize = 11.sp,
                                        color = SpotifyGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (isSelected) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SpotifyGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = SpotifyGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ACTIVE",
                                        color = SpotifyGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = "Select",
                                    color = SpotifyTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // App Info Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SpotifyCardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    ProfileOptionRow(
                        icon = Icons.Default.Info,
                        title = "About Lyra Music Player",
                        subtitle = "Version 1.0.0 • Spotify Edition",
                        onClick = { }
                    )
                }
            }
        }
    }

    // Login / Multi-Platform Account Dialog
    if (showLoginDialog) {
        var selectedTab by remember { mutableIntStateOf(0) } // 0: YouTube, 1: Spotify, 2: iTunes, 3: Profile Settings
        var inputEmail by remember { mutableStateOf(currentEmail) }
        var inputPassword by remember { mutableStateOf("") }
        var inputDisplayName by remember { mutableStateOf(currentUserName) }

        val tabs = listOf("YouTube", "Spotify", "iTunes", "Profile")

        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            containerColor = SpotifyCardBg,
            title = {
                Column {
                    Text(
                        text = "Account & Extension Login",
                        color = SpotifyTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect email/password to auto-load saved playlists, uploaded music & suggested songs",
                        color = SpotifyTextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFF282828),
                        contentColor = SpotifyGreen,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = SpotifyGreen
                                )
                            }
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == index) SpotifyGreen else SpotifyTextSecondary
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val (targetExtId, platformName) = when (selectedTab) {
                        0 -> Pair("youtube_music_preset", "YouTube Music")
                        1 -> Pair("spotify_music_preset", "Spotify Music")
                        2 -> Pair("itunes_music_preset", "iTunes Apple Music")
                        else -> Pair("", "Lyra Profile")
                    }

                    if (selectedTab in 0..2) {
                        val connectedAccount = accounts[targetExtId]?.takeIf { it.isLoggedIn }

                        if (connectedAccount != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2822)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SpotifyGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Connected as ${connectedAccount.username}",
                                            fontWeight = FontWeight.Bold,
                                            color = SpotifyTextPrimary,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Playlists, private uploads & suggested tracks are synced for $platformName.",
                                        fontSize = 12.sp,
                                        color = SpotifyTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            onLogoutAccount(targetExtId)
                                            Toast.makeText(context, "Logged out from $platformName", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Disconnect / Log Out", fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Re-enter email/password to switch or update account:",
                                fontSize = 12.sp,
                                color = SpotifyTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = inputEmail,
                            onValueChange = { inputEmail = it },
                            label = { Text("Gmail / Account Email", color = SpotifyTextSecondary) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SpotifyGreen)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = Color(0xFF404040),
                                focusedTextColor = SpotifyTextPrimary,
                                unfocusedTextColor = SpotifyTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = inputPassword,
                            onValueChange = { inputPassword = it },
                            label = { Text("Password", color = SpotifyTextSecondary) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SpotifyGreen)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = Color(0xFF404040),
                                focusedTextColor = SpotifyTextPrimary,
                                unfocusedTextColor = SpotifyTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (inputEmail.isBlank()) {
                                    Toast.makeText(context, "Please enter your Gmail / Email", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val pwdToken = if (inputPassword.isNotBlank()) inputPassword else "session_token_123"
                                val channel = if (targetExtId.contains("youtube")) "UC_main_channel" else "main_account"

                                onSaveAccount(targetExtId, inputEmail.trim(), channel, pwdToken)
                                currentEmail = inputEmail.trim()
                                isLoggedIn = true
                                Toast.makeText(
                                    context,
                                    "Logged into $platformName! Saved playlists, uploaded music & suggested tracks loaded into Your Library.",
                                    Toast.LENGTH_LONG
                                ).show()
                                showLoginDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log In & Sync $platformName", fontWeight = FontWeight.Bold)
                        }

                    } else {
                        // Profile display name & general settings tab
                        OutlinedTextField(
                            value = inputDisplayName,
                            onValueChange = { inputDisplayName = it },
                            label = { Text("Display Name", color = SpotifyTextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = Color(0xFF404040),
                                focusedTextColor = SpotifyTextPrimary,
                                unfocusedTextColor = SpotifyTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = inputEmail,
                            onValueChange = { inputEmail = it },
                            label = { Text("Primary Email Address", color = SpotifyTextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpotifyGreen,
                                unfocusedBorderColor = Color(0xFF404040),
                                focusedTextColor = SpotifyTextPrimary,
                                unfocusedTextColor = SpotifyTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                currentUserName = inputDisplayName.ifBlank { "Music Lover" }
                                currentEmail = inputEmail.ifBlank { "user@example.com" }
                                isLoggedIn = true
                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                showLoginDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Profile", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Close", color = SpotifyTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SpotifyGreen,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SpotifyTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SpotifyTextSecondary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SpotifyTextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

