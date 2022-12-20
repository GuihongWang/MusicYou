package com.kyant.musicyou.screens.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import com.kyant.ncmapi.data.Song
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rank: Int = -1,
    onLongClick: (() -> Unit)? = null,
    color: Color = 99.n1 withNight 10.n1
) {
    val imageSize = with(LocalDensity.current) { 48.dp.toPx().roundToInt() }
    Box {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(SmoothRoundedCornerShape(4.dp))
                .background(color)
                .then(
                    if (onLongClick != null) {
                        Modifier.combinedClickable(onLongClick = onLongClick) { onClick() }
                    } else {
                        Modifier.clickable { onClick() }
                    }
                )
                .padding(24.dp, 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "${song.album.imageUrl}?param=${imageSize}y$imageSize",
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = song.artists.joinToString { it.name },
                    color = 40.n1 withNight 70.n1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (song.fee == 1 || song.fee == 4) {
                Text(
                    text = "VIP",
                    color = 40.n1 withNight 70.n1,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        if (rank != -1) {
            Text(
                text = rank.toString(),
                modifier = Modifier.padding(6.dp, 8.dp),
                color = 40.n1 withNight 70.n1,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
