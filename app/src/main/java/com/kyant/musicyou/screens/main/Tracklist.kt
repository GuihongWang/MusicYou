package com.kyant.musicyou.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyant.monet.a1
import com.kyant.monet.a2
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.theme.Fonts
import com.kyant.musicyou.ui.SmoothCircleShape
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import com.kyant.musicyou.utils.tickVibrate
import com.kyant.musicyou.utils.withException
import com.kyant.ncmapi.PlaylistApi
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppViewModel.Tracklist() {
    val tracklist = tracklist ?: return
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tracklistSongs = tracklistSongs.collectAsState().value
    val isSubscribed = myTracklists.collectAsState().value.map { it.id }.contains(tracklist.id)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = tracklist.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize()
                .padding(24.dp)
                .clip(SmoothRoundedCornerShape(32.dp, 0.5f))
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = tracklist.name,
                modifier = Modifier.fillMaxWidth(),
                color = 20.a1 withNight 92.a2,
                fontWeight = FontWeight.Bold,
                fontFamily = Fonts.googleSansFontFamily,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "${tracklist.trackCount} 首歌曲",
                modifier = Modifier.fillMaxWidth(),
                color = 20.a1 withNight 92.a2,
                fontFamily = Fonts.googleSansFontFamily,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
        }
        LazyRow(
            modifier = Modifier.height(48.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                tracklistSongs?.data?.take(5) ?: emptyList(),
                key = { it.id }
            ) {
                val imageSize = with(LocalDensity.current) { 48.dp.toPx().roundToInt() }
                AsyncImage(
                    model = "${it.album.imageUrl}?param=${imageSize}y$imageSize",
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = isSubscribed) {
                Icon(
                    imageVector = if (it) Icons.Outlined.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            scope.launch(Dispatchers.IO) {
                                context.withException {
                                    PlaylistApi.subscribeOrUnsubscribe(
                                        id = tracklist.id,
                                        subscribe = !isSubscribed,
                                        cookie = login?.cookie
                                    )
                                }
                                myTracklistsIncrement++
                            }
                            context.tickVibrate()
                        }
                        .padding(12.dp)
                        .size(24.dp),
                    tint = if (it) Color.Red else LocalContentColor.current
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(SmoothCircleShape)
                    .background(99.n1 withNight 30.n1)
                    .clickable {
                        mainNavController.navigate("tracklist_songs")
                    }
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Audiotrack,
                    contentDescription = null
                )
                Text(text = "歌曲列表")
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(SmoothCircleShape)
                    .background(85.a1 withNight 90.a1)
                    .clickable {
                        scope.launch(Dispatchers.IO) {
                            playTracklistSongs()
                        }
                    }
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlaylistPlay,
                    contentDescription = null,
                    tint = 0.n1
                )
                Text(
                    text = "播放",
                    color = 0.n1
                )
            }
        }
    }
}
