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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.input.pointer.pointerInput
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

context (BoxScope)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppViewModel.BottomNowPlayingBar() {
    val song = currentSong ?: return
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .clip(SmoothRoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .background(99.n1 withNight 10.n1)
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectTapGestures {
                    isNowPlayingOpen = true
                }
            }
            .padding(16.dp, 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // album art
        AsyncImage(
            model = song.album.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(SmoothRoundedCornerShape(8.dp))
        )
        // song titles
        AnimatedContent(
            targetState = song,
            modifier = Modifier.weight(1f),
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = it.name,
                    color = 20.a1 withNight 92.a2,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Fonts.googleSansFontFamily,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = it.artists.joinToString { it.name },
                    color = 20.a1 withNight 92.a2,
                    fontFamily = Fonts.googleSansFontFamily,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        // play pause button
        CompositionLocalProvider(LocalIndication provides rememberRipple(color = 0.n1)) {
            AnimatedContent(
                targetState = isPlaying,
                modifier = Modifier.playPauseButton {
                    playOrPause()
                    context.tickVibrate()
                }
            ) {
                Icon(
                    painter = painterResource(id = if (it) R.drawable.ic_pause else R.drawable.ic_play),
                    contentDescription = null,
                    tint = 0.n1
                )
            }
        }
        // playlists button
        Icon(
            imageVector = Icons.Outlined.PlaylistPlay,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures {
                        mainNavController.navigate("playlists")
                    }
                }
                .padding(8.dp)
                .size(32.dp),
            contentDescription = null
        )
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
        .background(92.a1 withNight 90.a1)
        .clickable(onClick = onClick)
        .padding(16.dp)
        .size(24.dp)
}
