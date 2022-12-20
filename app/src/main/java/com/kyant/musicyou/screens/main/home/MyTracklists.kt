package com.kyant.musicyou.screens.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.main.components.ItemTile

@Composable
fun AppViewModel.MyTracklists() {
    val myTracklists = myTracklists.collectAsState().value
        .takeIf { it.isNotEmpty() } ?: return
    Text(
        text = "我的歌单",
        modifier = Modifier.padding(horizontal = 24.dp),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            myTracklists,
            key = { it.id }
        ) {
            ItemTile(
                imageUrl = it.imageUrl,
                label = it.name,
                onClick = {
                    tracklist = it
                    isRankingTracklist = false
                    mainNavController.navigate("tracklist")
                }
            )
        }
    }
}
