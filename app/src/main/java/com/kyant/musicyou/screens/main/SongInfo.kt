package com.kyant.musicyou.screens.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.PlayerViewModel
import com.kyant.musicyou.utils.toReadableTimeString

@Composable
fun PlayerViewModel.SongInfo() {
    val song = currentSong ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = song.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = song.artists.joinToString { it.name },
                style = MaterialTheme.typography.titleMedium
            )
        }
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "时长：约 ${song.duration.toReadableTimeString()}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "包含于专辑 ${song.album.name}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "网易云音乐 ID：${song.id}（音乐）",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(
                text = "艺术家",
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.titleLarge
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(song.artists) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(99.n1 withNight 30.n1)
                            .clickable {}
                            .padding(24.dp, 16.dp)
                            .animateContentSize()
                    ) {
                        Text(text = it.name)
                    }
                }
            }
        }
    }
}
