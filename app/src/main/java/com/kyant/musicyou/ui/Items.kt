package com.kyant.musicyou.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kyant.monet.n1
import com.kyant.monet.withNight

@Composable
fun <T> Items(
    items: List<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    key: ((index: Int, item: T) -> Any)? = null,
    contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    canLoadMore: Boolean = false,
    onLoadMore: () -> Unit = {},
    itemContent: @Composable LazyItemScope.(index: Int, item: T, shapeModifier: Modifier) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .clip(SmoothRoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp))
            .background(95.n1 withNight 0.n1),
        state = state,
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        itemsIndexed(items, key = key, contentType) { index, item ->
            itemContent(
                index,
                item,
                if (index != items.lastIndex) Modifier
                else Modifier.clip(SmoothRoundedCornerShape(4.dp, 4.dp, 32.dp, 32.dp))
            )
        }
        if (canLoadMore) {
            item {
                LaunchedEffect(Unit) {
                    onLoadMore()
                }
            }
        }
    }
}
