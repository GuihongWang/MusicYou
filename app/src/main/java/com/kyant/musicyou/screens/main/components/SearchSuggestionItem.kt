package com.kyant.musicyou.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.ui.SmoothRoundedCornerShape

@Composable
fun SearchSuggestionItem(
    suggestion: String,
    onClick: () -> Unit,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SmoothRoundedCornerShape(4.dp))
            .background(98.n1 withNight 10.n1)
            .clickable(onClick = onClick)
            .padding(16.dp, 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = suggestion,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium
        )
        Icon(
            imageVector = Icons.Outlined.NorthWest,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onCompleted)
                .padding(8.dp)
        )
    }
}
