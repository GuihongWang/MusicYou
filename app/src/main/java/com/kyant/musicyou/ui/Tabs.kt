package com.kyant.musicyou.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kyant.monet.a1
import com.kyant.monet.n1
import com.kyant.monet.withNight

@Composable
fun <T> Tabs(
    items: List<T>,
    label: (Int, T) -> String,
    selectedIndex: Int,
    onClick: (Int, T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex),
        contentPadding = PaddingValues(16.dp, 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(items) { index, item ->
            val selected = index == selectedIndex
            Text(
                text = label(index, item),
                modifier = Modifier
                    .clip(SmoothCircleShape)
                    .background(
                        animateColorAsState(
                            targetValue = if (selected) 40.a1 withNight 90.a1 else Color.Transparent
                        ).value
                    )
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onClick(index, item)
                        }
                    }
                    .padding(24.dp, 12.dp),
                color = animateColorAsState(
                    targetValue = if (selected) 100.n1 withNight 0.n1 else LocalContentColor.current
                ).value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
