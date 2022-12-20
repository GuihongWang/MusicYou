package com.kyant.musicyou.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.settings.components.SettingItem
import com.kyant.musicyou.screens.settings.components.SettingItemWithSwitch
import com.kyant.musicyou.ui.SmoothRoundedCornerShape

@Composable
fun AppViewModel.Player() {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "播放器",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(SmoothRoundedCornerShape(32.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                SettingItem(
                    title = "音效",
                    subtitle = "暂不支持",
                    onClick = {}
                )
                SettingItemWithSwitch(
                    title = "跳过 VIP 歌曲",
                    subtitle = "会员用户仍可播放",
                    checked = true,
                    onCheckedChange = {}
                )
            }
        }
    }
}
