package com.kyant.musicyou.screens.main.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import kotlin.math.roundToInt

@Composable
fun ItemTile(
    imageUrl: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageSize = with(LocalDensity.current) { 96.dp.toPx().roundToInt() }
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalIndication provides rememberRipple(color = 100.n1)) {
            AsyncImage(
                model = "$imageUrl?param=${imageSize}y$imageSize",
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(SmoothRoundedCornerShape(16.dp))
                    .clickable(onClick = onClick)
            )
        }
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth(),
            color = 20.n1 withNight 90.n1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
