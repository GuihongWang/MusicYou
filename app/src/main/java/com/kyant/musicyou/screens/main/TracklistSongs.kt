package com.kyant.musicyou.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.main.components.SongItem
import com.kyant.musicyou.ui.Items
import com.kyant.musicyou.ui.LoadingIndicator
import com.kyant.musicyou.ui.SplitLayoutScaffold
import com.kyant.musicyou.utils.withException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppViewModel.TracklistSongs() {
    val tracklist = tracklist ?: return
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tracklistSongs = tracklistSongs.collectAsState().value
    SplitLayoutScaffold(topContent = {
        Row(
            modifier = Modifier
                .height(72.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tracklist.name,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }) {
        if (tracklistSongs?.data?.isNotEmpty() == true) {
            Items(
                items = tracklistSongs.data,
                key = { _, song -> song.id },
                canLoadMore = true,
                onLoadMore = {
                    scope.launch(Dispatchers.IO) {
                        context.withException {
                            tracklistSongs.load()
                        }
                    }
                }
            ) { index, song, shapeModifier ->
                SongItem(
                    song = song,
                    onClick = {
                        scope.launch {
                            addToMainPlaylistAndPlay(song)
                        }
                    },
                    modifier = shapeModifier,
                    rank = if (isRankingTracklist) index + 1 else -1
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
    }
}
