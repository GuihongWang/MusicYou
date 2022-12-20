package com.kyant.musicyou.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.kyant.monet.n1
import com.kyant.monet.n2
import com.kyant.monet.withNight

@Composable
fun SplitLayoutScaffold(
    topContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(92.n1 withNight 0.n1)
            .statusBarsPadding()
    ) {
        Box {
            topContent()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 24.dp,
                    shape = SmoothRoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp),
                    ambientColor = 70.n1 withNight 30.n1,
                    spotColor = 80.n1 withNight 20.n1
                )
                .background(95.n2 withNight 10.n1)
        ) {
            content()
        }
    }
}
