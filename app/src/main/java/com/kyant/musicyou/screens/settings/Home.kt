package com.kyant.musicyou.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.settings.components.SettingCatalogItem
import com.kyant.musicyou.ui.SmoothCircleShape
import com.kyant.musicyou.ui.SmoothRoundedCornerShape

@Composable
fun AppViewModel.Home() {
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
                text = "设置",
                modifier = Modifier.padding(horizontal = 24.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )
            FakeSearchBar()
            Text(
                text = "基础",
                modifier = Modifier.padding(horizontal = 40.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(SmoothRoundedCornerShape(32.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    SettingCatalog("account", "账户", "网易云音乐账户、用户资料"),
                    SettingCatalog("player", "播放器", "音效"),
                    SettingCatalog("customization", "个性化", "应用主题、颜色样式")
                ).forEach { (id, title, subtitle) ->
                    SettingCatalogItem(
                        title = title,
                        subtitle = subtitle,
                        onClick = { settingsNavController.navigate(id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FakeSearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(56.dp)
                .clip(SmoothCircleShape)
                .background(99.n1 withNight 20.n1)
                .clickable {}
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                modifier = Modifier.offset(y = 1.dp),
                contentDescription = null
            )
            Text(
                text = "搜索设置项",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable {}
                .padding(12.dp)
                .size(24.dp)
        )
    }
}

private data class SettingCatalog(
    val id: String,
    val title: String,
    val subtitle: String
)
