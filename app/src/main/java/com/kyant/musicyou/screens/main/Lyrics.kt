package com.kyant.musicyou.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.monet.a1
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.PlayerViewModel
import com.kyant.musicyou.media.currentLyricIndex
import com.kyant.musicyou.media.toMillisecondsOrNull
import com.kyant.musicyou.theme.Fonts
import com.kyant.musicyou.ui.LoadingIndicator
import com.kyant.musicyou.ui.SmoothRoundedCornerShape

@Composable
fun PlayerViewModel.Lyrics() {
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = (currentLyricIndex() - 1).coerceAtLeast(0)
    )
    var scrollingLocked by remember { mutableStateOf(true) }
    var position by remember { mutableStateOf(0f) }
    val currentIndex = currentLyricIndex()
    LaunchedEffect(currentIndex) {
        if (!scrollingLocked) {
            if (!state.isScrollInProgress) {
                if (currentIndex in state.layoutInfo.visibleItemsInfo.map { it.key as Int } && currentIndex != 0) {
                    state.animateScrollBy(
                        position,
                        animationSpec = tween(800, easing = EaseInOut)
                    )
                } else {
                    state.animateScrollToItem((currentIndex - 1).coerceAtLeast(0))
                }
            }
        }
    }
    LaunchedEffect(currentSong) {
        if (!scrollingLocked) {
            state.scrollToItem(0)
        }
        scrollingLocked = false
    }
    if (currentLyrics.isNotEmpty()) {
        LazyColumn(
            state = state,
            contentPadding = WindowInsets.systemBars.asPaddingValues()
        ) {
            itemsIndexed(
                currentLyrics.toList(),
                key = { index, _ -> index }
            ) { index, (time, lyric) ->
                val isCurrentLyric = index == currentIndex
                Text(
                    text = lyric,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = animateDpAsState(
                                targetValue = if (isCurrentLyric && isPlaying) 16.dp else 0.dp,
                                animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
                            ).value
                        )
                        .clip(SmoothRoundedCornerShape(24.dp))
                        .clickable {
                            time
                                .toMillisecondsOrNull()
                                ?.let { seekTo(it) }
                        }
                        .then(
                            if (index == currentIndex - 1) {
                                Modifier.onGloballyPositioned {
                                    position = it.positionInRoot().y
                                }
                            } else Modifier
                        )
                        .padding(24.dp),
                    color = animateColorAsState(
                        targetValue = if (isCurrentLyric) 40.a1 withNight 80.a1 else 70.n1 withNight 40.n1,
                        animationSpec = tween(600, delayMillis = 200, easing = EaseInOut)
                    ).value,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Fonts.googleSansFontFamily,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    }
}
