package com.kyant.musicyou.screens.main.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyant.monet.a1
import com.kyant.monet.a2
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.R
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.theme.Fonts
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import com.kyant.musicyou.utils.tickVibrate

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppViewModel.NowPlayingCard() {
    val context = LocalContext.current
    Text(
        text = "正在播放",
        modifier = Modifier.padding(horizontal = 24.dp),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium
    )
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(SmoothRoundedCornerShape(32.dp))
    ) {
        Column {
            currentSong?.let { song ->
                CompositionLocalProvider(LocalIndication provides rememberRipple(color = 40.a1 withNight 100.n1)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(92.a1 withNight 15.n1)
                            .clickable {
                                mainNavController.navigate("now_playing")
                            }
                            .padding(16.dp, 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // album art
                        AsyncImage(
                            model = song.album.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(SmoothRoundedCornerShape(24.dp))
                        )
                        // song titles
                        AnimatedContent(
                            targetState = song,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                    slideInHorizontally(
                                        animationSpec = spring(stiffness = Spring.StiffnessLow)
                                    ) { it / 2 } with
                                    fadeOut(animationSpec = tween(90)) +
                                        slideOutHorizontally(
                                            animationSpec = spring(stiffness = Spring.StiffnessLow)
                                        )
                            }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = it.name,
                                    color = 20.a1 withNight 92.a2,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Fonts.googleSansFontFamily,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = it.artists.joinToString { it.name },
                                    color = 20.a1 withNight 92.a2,
                                    fontFamily = Fonts.googleSansFontFamily,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
                // progress indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(100.n1 withNight 30.n1)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentPositionPercentage)
                            .fillMaxHeight()
                            .background(40.a1 withNight 90.a1)
                    )
                }
            }
            // playlists button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(98.a1 withNight 20.n1)
                    .clickable {
                        mainNavController.navigate("playlists")
                    }
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlaylistPlay,
                    contentDescription = null
                )
                Text(text = "播放列表")
            }
        }
        // play pause button
        if (currentSong != null) {
            CompositionLocalProvider(LocalIndication provides rememberRipple(color = 100.n1 withNight 0.n1)) {
                AnimatedContent(
                    targetState = isPlaying,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp, 30.dp)
                        .playPauseButton {
                            playOrPause()
                            context.tickVibrate()
                        }
                ) {
                    Icon(
                        painter = painterResource(id = if (it) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = null,
                        tint = 100.n1 withNight 0.n1
                    )
                }
            }
        }
    }
}

context (AppViewModel)
private fun Modifier.playPauseButton(onClick: () -> Unit) = composed {
    val shape = SmoothRoundedCornerShape(
        animateDpAsState(
            targetValue = if (isPlaying) 16.dp else 40.dp,
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        ).value,
        animateFloatAsState(
            targetValue = if (isPlaying) 0.5f else 0f,
            animationSpec = spring(
                stiffness = if (isPlaying) Spring.StiffnessLow else Spring.StiffnessMedium
            )
        ).value
    )
    this
        .clip(shape)
        .background(40.a1 withNight 90.a1)
        .clickable(onClick = onClick)
        .padding(16.dp)
        .size(24.dp)
}
