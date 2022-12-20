package com.kyant.musicyou.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kyant.monet.a1
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.PlayerViewModel
import kotlin.math.roundToLong

@Composable
fun PlayerViewModel.ProgressBar(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    var width by remember { mutableStateOf(0.dp) }
    var isSeekBarDragging by remember { mutableStateOf(false) }
    var draggedDelta by remember { mutableStateOf(0f) }
    val state = rememberDraggableState { draggedDelta += it }
    val fraction = with(density) {
        if ((isSeekBarDragging || isBuffering) && width != 0.dp) draggedDelta.toDp() / width
        else currentPositionPercentage
    }.coerceIn(0f..1f)
    preparedSeekingPosition = (fraction * currentDuration).roundToLong().takeIf { isSeekBarDragging }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .onSizeChanged { with(density) { width = it.width.toDp() } }
            .clip(CircleShape)
            .background(99.n1 withNight 30.n1)
            .draggable(
                state = state,
                orientation = Orientation.Horizontal,
                onDragStarted = {
                    draggedDelta = it.x
                    isSeekBarDragging = true
                },
                onDragStopped = {
                    seekToPercentage(fraction)
                    isSeekBarDragging = false
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(40.a1 withNight 90.a1)
        )
    }
}
