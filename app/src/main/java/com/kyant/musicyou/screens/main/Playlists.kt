package com.kyant.musicyou.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.PlaylistRemove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kyant.monet.a1
import com.kyant.monet.a3
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.PlayerViewModel
import com.kyant.musicyou.screens.main.components.SongItem
import com.kyant.musicyou.ui.BigIconButton
import com.kyant.musicyou.ui.Items
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import com.kyant.musicyou.ui.SplitLayoutScaffold
import com.kyant.musicyou.ui.Tabs
import com.kyant.musicyou.utils.tickVibrate
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayerViewModel.Playlists() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = (currentSongIndex - 1).coerceAtLeast(0)
    )
    var selectedIndex by remember { mutableStateOf(currentPlaylistIndex) }
    val playlist = if (selectedIndex == -1) mainPlaylist
    else playlists.getOrElse(selectedIndex) { emptyList() }
    LaunchedEffect(currentPlaylistIndex) {
        selectedIndex = currentPlaylistIndex
    }
    LaunchedEffect(currentPlaylistIndex, currentSong, selectedIndex) {
        if (!state.isScrollInProgress && selectedIndex == currentPlaylistIndex) {
            state.animateScrollToItem((currentSongIndex - 1).coerceAtLeast(0))
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        SplitLayoutScaffold(topContent = {
            Tabs(
                items = listOf(-1) + List(playlists.size) { index -> index },
                label = { _, index -> if (index == -1) "主列" else "#${(index + 1)}" },
                selectedIndex = selectedIndex + 1,
                onClick = { _, index -> selectedIndex = index },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }) {
            if (playlist.isNotEmpty()) {
                Items(
                    items = playlist,
                    state = state,
                    key = { _, song -> song.id }
                ) { _, song, shapeModifier ->
                    SongItem(
                        song = song,
                        onClick = {
                            scope.launch {
                                seekToPlaylist(selectedIndex)
                                seekToSongAndPlay(currentPlaylist.indexOf(song))
                            }
                        },
                        modifier = shapeModifier.then(
                            if (selectedIndex == -1) Modifier.animateItemPlacement()
                            else Modifier
                        ),
                        onLongClick = {
                            if (selectedIndex == -1) {
                                removeFromMainPlaylist(song)
                                context.tickVibrate()
                            }
                        },
                        color = animateColorAsState(
                            targetValue = if (selectedIndex == currentPlaylistIndex && song == currentSong) {
                                90.a1 withNight 30.n1
                            } else 99.n1 withNight 10.n1
                        ).value
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "没有歌曲",
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(SmoothRoundedCornerShape(24.dp))
                            .background(99.n1 withNight 25.n1)
                            .padding(24.dp, 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        AnimatedContent(
            targetState = selectedIndex == -1,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 24.dp, bottom = 24.dp)
        ) {
            if (it) {
                // clear main playlist
                BigIconButton(
                    imageVector = Icons.Outlined.ClearAll,
                    contentDescription = null,
                    onClick = {
                        with(context) {
                            clearMainPlaylist()
                        }
                    }
                )
            } else {
                // remove current playlist
                BigIconButton(
                    imageVector = Icons.Outlined.PlaylistRemove,
                    contentDescription = null,
                    onClick = {
                        scope.launch {
                            with(context) {
                                removePlaylistAt(selectedIndex)
                            }
                        }
                    },
                    color = 80.a3 withNight 90.a3
                )
            }
        }
    }
}
