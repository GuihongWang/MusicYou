package com.kyant.musicyou.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.monet.a1
import com.kyant.monet.n1
import com.kyant.monet.n2
import com.kyant.monet.withNight
import com.kyant.musicyou.R
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.main.home.BottomNowPlayingBar
import com.kyant.musicyou.screens.main.home.MyTracklists
import com.kyant.musicyou.screens.main.home.NowPlayingCard
import com.kyant.musicyou.screens.main.home.RecommendedSongs
import com.kyant.musicyou.screens.main.home.RecommendedTracklists
import com.kyant.musicyou.screens.main.home.Toplists
import com.kyant.musicyou.ui.SmoothRoundedCornerShape

@Composable
fun AppViewModel.Home() {
    val density = LocalDensity.current
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 88.dp)
                .systemBarsPadding()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AppHeadline()
            NowPlayingCard()
            MyTracklists()
            RecommendedSongs()
            RecommendedTracklists()
            Toplists()
        }
        TransformableNowPlayingContainer()
        TransformableSearchContainer()
        BackHandler(enabled = isSearchOpen) {
            isSearchOpen = false
        }
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + slideInVertically { with(density) { -56.dp.roundToPx() } },
            exit = fadeOut() + slideOutVertically { with(density) { -56.dp.roundToPx() } }
        ) {
            MenuDialog()
        }
        BackHandler(enabled = isNowPlayingOpen) {
            isNowPlayingOpen = false
        }
        BackHandler(enabled = isMenuOpen) {
            isMenuOpen = false
        }
    }
}

@Composable
private fun AppViewModel.AppHeadline() {
    Spacer(modifier = Modifier)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge
        )
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { isMenuOpen = true }
                .padding(12.dp)
                .size(24.dp)
        )
    }
    Spacer(modifier = Modifier)
}

context (BoxScope)
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppViewModel.TransformableNowPlayingContainer() {
    val bottomNowPlayingBar = remember {
        movableContentOf { BottomNowPlayingBar() }
    }
    val nowPlaying = remember {
        movableContentOf { NowPlaying() }
    }

    AnimatedContent(
        targetState = isNowPlayingOpen,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .clip(SmoothRoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .background(
                animateColorAsState(
                    targetValue = if (isNowPlayingOpen) 95.n2 withNight 10.n1
                    else 92.a1 withNight 15.n1
                ).value
            ),
        transitionSpec = {
            fadeIn(animationSpec = tween(220, delayMillis = 90)) with
                fadeOut(animationSpec = tween(90))
        },
        contentAlignment = Alignment.Center
    ) {
        if (it) {
            nowPlaying()
        } else {
            bottomNowPlayingBar()
        }
    }
}

context (BoxScope)
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppViewModel.TransformableSearchContainer() {
    val icon = remember {
        movableContentOf {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier
                    .clickable { isSearchOpen = true }
                    .padding(16.dp)
                    .size(32.dp),
                tint = 0.n1
            )
        }
    }
    val search = remember {
        movableContentOf { Search() }
    }

    AnimatedVisibility(
        visible = !isNowPlayingOpen,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(
                end = animateDpAsState(
                    targetValue = if (isSearchOpen) 0.dp else 16.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ).value.coerceAtLeast(0.dp),
                bottom = animateDpAsState(
                    targetValue = if (isSearchOpen) 0.dp else 104.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ).value.coerceAtLeast(0.dp)
            ),
        enter = fadeIn(),
        exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
    ) {
        AnimatedContent(
            targetState = isSearchOpen,
            modifier = Modifier
                .navigationBarsPadding()
                .clip(
                    SmoothRoundedCornerShape(
                        animateDpAsState(targetValue = if (isSearchOpen) 40.dp else 24.dp).value
                    )
                )
                .background(
                    animateColorAsState(
                        targetValue = if (isSearchOpen) 95.n2 withNight 10.n1
                        else 85.a1 withNight 90.a1
                    ).value
                ),
            transitionSpec = {
                fadeIn(animationSpec = tween(600, delayMillis = 90)) with
                    fadeOut(animationSpec = tween(90))
            },
            contentAlignment = Alignment.Center
        ) {
            if (it) {
                search()
            } else {
                icon()
            }
        }
    }
}
