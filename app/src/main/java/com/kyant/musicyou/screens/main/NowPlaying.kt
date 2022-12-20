package com.kyant.musicyou.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.kyant.monet.a1
import com.kyant.monet.a2
import com.kyant.monet.withNight
import com.kyant.musicyou.R
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.data.PlayerViewModel
import com.kyant.musicyou.media.currentLyricIndex
import com.kyant.musicyou.media.currentLyricOrNull
import com.kyant.musicyou.media.findLyricOrNull
import com.kyant.musicyou.screens.main.components.SquigglyProgressBar
import com.kyant.musicyou.theme.Fonts
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import com.kyant.musicyou.utils.tickVibrate
import com.kyant.musicyou.utils.toReadableTimeString
import com.kyant.musicyou.utils.withException
import com.kyant.ncmapi.SongApi
import com.kyant.ncmapi.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppViewModel.NowPlaying() {
    val song = currentSong ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures() }
            .systemBarsPadding()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        AnimatedSongTitles(song = song)
        AlbumArt(song = song)
        MediaControls()
        AnimatedCurrentLyric()
        ProgressBar()
        PlayerControls(song = song)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedSongTitles(song: Song) {
    AnimatedContent(
        targetState = song,
        transitionSpec = {
            fadeIn(tween(220, delayMillis = 90)) +
                slideInHorizontally(spring(stiffness = Spring.StiffnessLow)) { it / 2 } with
                fadeOut(tween(90)) +
                    slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessLow))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = it.name,
                color = 20.a1 withNight 92.a2,
                fontWeight = FontWeight.Bold,
                fontFamily = Fonts.googleSansFontFamily,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = it.artists.joinToString { it.name },
                color = 20.a1 withNight 92.a2,
                fontFamily = Fonts.googleSansFontFamily,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun AlbumArt(song: Song) {
    AsyncImage(
        model = song.album.imageUrl,
        contentDescription = null,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .aspectRatio(1f)
            .fillMaxSize()
            .clip(SmoothRoundedCornerShape(32.dp, 0.5f))
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppViewModel.AnimatedCurrentLyric() {
    AnimatedContent(
        targetState = if (preparedSeekingPosition == null) {
            currentLyricIndex() to currentLyricOrNull()
        } else {
            -1 to findLyricOrNull(preparedSeekingPosition)
        },
        modifier = Modifier,
        transitionSpec = {
            if (preparedSeekingPosition == null) {
                fadeIn() + slideInVertically(spring(stiffness = Spring.StiffnessLow)) { it } with
                    fadeOut() + slideOutVertically(spring(stiffness = Spring.StiffnessVeryLow)) { -it }
            } else {
                fadeIn(tween(0)) with fadeOut(tween(0))
            }
        }
    ) { (_, lyric) ->
        Text(
            text = lyric ?: " ", // TODO: bug fixes
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures {
                        mainNavController.navigate("lyrics")
                    }
                }
                .padding(24.dp),
            color = 20.a1 withNight 92.a2,
            fontWeight = FontWeight.Medium,
            fontFamily = Fonts.googleSansFontFamily,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerViewModel.MediaControls() {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // skip previous button
        Icon(
            painter = painterResource(id = R.drawable.ic_skip_previous),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        seekToPreviousSong()
                        context.tickVibrate()
                    }
                }
        )
        // play pause button
        AnimatedContent(targetState = isPlaying) {
            Icon(
                painter = painterResource(id = if (it) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            playOrPause()
                            context.tickVibrate()
                        }
                    }
            )
        }
        // skip next button
        Icon(
            painter = painterResource(id = R.drawable.ic_skip_next),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .pointerInput(hasNextSong) {
                    detectTapGestures {
                        if (hasNextSong) {
                            seekToNextSong()
                            context.tickVibrate()
                        }
                    }
                }
                .alpha(if (hasNextSong) 1f else 0.38f)
        )
    }
}

@Composable
private fun PlayerViewModel.ProgressBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (preparedSeekingPosition ?: currentPosition).toReadableTimeString(),
            modifier = Modifier.animateContentSize(
                spring(stiffness = Spring.StiffnessLow)
            ),
            style = MaterialTheme.typography.labelMedium
        )
        SquigglyProgressBar(modifier = Modifier.weight(1f))
        Text(
            text = currentDuration.toReadableTimeString(),
            modifier = Modifier.animateContentSize(
                spring(stiffness = Spring.StiffnessLow)
            ),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppViewModel.PlayerControls(song: Song) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLiked = likelistIds.collectAsState().value.contains(song.id)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // shuffle mode button
        Icon(
            imageVector = Icons.Outlined.Shuffle,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    changeShuffleMode()
                    context.tickVibrate()
                }
                .padding(12.dp)
                .size(24.dp)
                .alpha(if (shuffleMode) 1f else 0.38f)
        )
        // repeat mode button
        Icon(
            imageVector = when (repeatMode) {
                Player.REPEAT_MODE_OFF -> Icons.Outlined.Repeat
                Player.REPEAT_MODE_ALL -> Icons.Outlined.Repeat
                Player.REPEAT_MODE_ONE -> Icons.Outlined.RepeatOne
                else -> error("Invalid repeat mode")
            },
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    changeRepeatMode()
                    context.tickVibrate()
                }
                .padding(12.dp)
                .size(24.dp)
                .alpha(if (repeatMode == Player.REPEAT_MODE_OFF) 0.38f else 1f)
        )
        // info button
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    mainNavController.navigate("song_info")
                }
                .padding(12.dp)
                .size(24.dp)
        )
        // timer button
        Icon(
            imageVector = Icons.Outlined.Timer,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    context.tickVibrate()
                }
                .padding(12.dp)
                .size(24.dp)
                .alpha(0.38f)
        )
        // like button
        AnimatedContent(targetState = isLiked) {
            Icon(
                imageVector = if (it) Icons.Outlined.Favorite
                else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        scope.launch(Dispatchers.IO) {
                            context.withException {
                                SongApi.likeOrUnlike(
                                    id = song.id,
                                    like = !it,
                                    cookie = login?.cookie
                                )
                            }
                            likelistIdsIncrement++
                        }
                        context.tickVibrate()
                    }
                    .padding(12.dp)
                    .size(24.dp),
                tint = if (it) Color.Red else LocalContentColor.current
            )
        }
    }
}
