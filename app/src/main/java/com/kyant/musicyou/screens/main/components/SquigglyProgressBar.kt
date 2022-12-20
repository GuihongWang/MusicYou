package com.kyant.musicyou.screens.main.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.kyant.monet.a1
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.PlayerViewModel
import kotlin.math.log
import kotlin.math.roundToLong

@Composable
fun PlayerViewModel.SquigglyProgressBar(modifier: Modifier = Modifier) {
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
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        // squiggly progress bar (foreground)
        val color = 40.a1 withNight 80.a1
        val amplitude = with(density) { 4.dp.toPx() }
        val animatedAmplitude = animateFloatAsState(
            targetValue = if (isPlaying && !isSeekBarDragging) amplitude else 0f,
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        ).value
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .offset(y = 4.dp)
                .clipWavyProgressBar(amplitude, fraction)
        ) {
            drawWaves(fraction, animatedAmplitude, width, color)
        }
        // seek bar (background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .onSizeChanged { with(density) { width = it.width.toDp() } }
                .seekBarBackground(fraction)
                .pointerInput(Unit) {
                    detectTapGestures {
                        seekToPercentage(with(density) { it.x / width.toPx() })
                    }
                }
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
        )
        // thumb
        Box(
            modifier = Modifier
                .offset(x = fraction * width - 8.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(40.a1 withNight 80.a1)
        )
    }
}

private fun Modifier.clipWavyProgressBar(
    amplitude: Float,
    percentage: Float
) = clip(object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                0f,
                -amplitude,
                percentage * size.width,
                amplitude
            )
        )
    }
})

private fun DrawScope.drawWaves(
    percentage: Float,
    animatedAmplitude: Float,
    width: Dp,
    color: Color
) {
    val period = 12.dp.toPx()
    val phaseShift = -(percentage * 5000).mod(period * 2)
    val frequency = (percentage * (width.toPx() / (period * 2))).toInt() + 2
    drawPath(
        path = Path().apply {
            for (i in 0..frequency) {
                val adaptedAmplitude =
                    (animatedAmplitude * log(phaseShift / (period * 2) + i + 2.0, frequency + 1.0)).toFloat()
                moveTo(phaseShift + period * 2 * i, 0f)
                relativeQuadraticBezierTo(
                    period / 2,
                    adaptedAmplitude,
                    period,
                    0f
                )
                relativeQuadraticBezierTo(
                    period / 2,
                    -adaptedAmplitude,
                    period,
                    0f
                )
            }
            close()
        },
        color = color,
        style = Stroke(width = 3.dp.toPx())
    )
}

private fun Modifier.seekBarBackground(percentage: Float) = composed {
    background(
        99.n1 withNight 80.n1,
        shape = object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density
            ): Outline {
                return Outline.Rounded(
                    with(density) {
                        RoundRect(
                            rect = Rect(
                                percentage * size.width,
                                6.5.dp.toPx(),
                                size.width,
                                9.5.dp.toPx()
                            ),
                            topLeft = CornerRadius.Zero,
                            topRight = CornerRadius(1.5.dp.toPx()),
                            bottomRight = CornerRadius(1.5.dp.toPx()),
                            bottomLeft = CornerRadius.Zero
                        )
                    }
                )
            }
        }
    )
}
