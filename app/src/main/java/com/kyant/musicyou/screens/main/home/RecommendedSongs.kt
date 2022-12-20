package com.kyant.musicyou.screens.main.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.main.components.ItemTile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppViewModel.RecommendedSongs() {
    val recommendedSongs = recommendedSongs.collectAsState().value
        .takeIf { it.isNotEmpty() } ?: return
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "推荐歌曲",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Icon(
            imageVector = Icons.Rounded.PlayArrow,
            contentDescription = null,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures {
                    scope.launch(Dispatchers.IO) {
                        playRecommendedSongs()
                    }
                }
            }
        )
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            recommendedSongs,
            key = { it.id }
        ) {
            ItemTile(
                imageUrl = it.album.imageUrl,
                label = it.name,
                onClick = {
                    scope.launch {
                        addToMainPlaylistAndPlay(it)
                    }
                }
            )
        }
    }
}
