package com.kyant.musicyou.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.kyant.monet.n1
import com.kyant.monet.withNight
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.main.components.ArtistItem
import com.kyant.musicyou.screens.main.components.SearchSuggestionItem
import com.kyant.musicyou.screens.main.components.SongItem
import com.kyant.musicyou.screens.main.components.TracklistItem
import com.kyant.musicyou.ui.Items
import com.kyant.musicyou.ui.LoadingIndicator
import com.kyant.musicyou.ui.SmoothRoundedCornerShape
import com.kyant.musicyou.ui.SplitLayoutScaffold
import com.kyant.musicyou.ui.Tabs
import com.kyant.musicyou.ui.TextField
import com.kyant.musicyou.utils.isKeyboardOpen
import com.kyant.musicyou.utils.withException
import com.kyant.ncmapi.data.Artist
import com.kyant.ncmapi.data.Playlist
import com.kyant.ncmapi.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppViewModel.Search() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val searchSuggestions = searchSuggestions.collectAsState().value
    val searchResult = searchResult.collectAsState().value
    LaunchedEffect(query.text) {
        if (query.text.isBlank()) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    SplitLayoutScaffold(topContent = {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.offset(y = 1.dp)
                )
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholderText = "搜索内容",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                    })
                )
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { query = query.copy(text = "") }
                        .padding(12.dp)
                        .size(24.dp)
                )
            }
            AnimatedVisibility(
                visible = searchSuggestions.isNotEmpty() && isKeyboardOpen(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(SmoothRoundedCornerShape(32.dp))
            ) {
                LazyColumn(
                    modifier = Modifier.animateContentSize(spring(stiffness = Spring.StiffnessLow)),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        searchSuggestions,
                        key = { it }
                    ) { suggestion ->
                        SearchSuggestionItem(
                            suggestion = suggestion,
                            onClick = {
                                query = query.copy(
                                    text = suggestion,
                                    selection = TextRange(suggestion.length)
                                )
                                keyboardController?.hide()
                            },
                            onCompleted = {
                                query = query.copy(
                                    text = suggestion,
                                    selection = TextRange(suggestion.length)
                                )
                            },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
            Tabs(
                items = listOf("歌曲", "歌单", "艺人"),
                label = { _, label -> label },
                selectedIndex = searchType,
                onClick = { index, _ -> searchType = index },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }) {
        if (searchResult?.data?.isNotEmpty() == true) {
            Items(
                items = searchResult.data,
                key = { _, item ->
                    when (item) {
                        is Song -> item.id
                        is Playlist -> item.id
                        is Artist -> item.id
                        else -> error("Invalid search type")
                    }
                },
                canLoadMore = true,
                onLoadMore = {
                    scope.launch(Dispatchers.IO) {
                        context.withException {
                            searchResult.load()
                        }
                    }
                }
            ) { _, item, shapeModifier ->
                when (item) {
                    is Song -> SongItem(
                        song = item,
                        onClick = {
                            scope.launch {
                                addToMainPlaylistAndPlay(item)
                            }
                        },
                        modifier = shapeModifier
                    )
                    is Playlist -> TracklistItem(
                        playlist = item,
                        onClick = {
                            tracklist = item
                            mainNavController.navigate("tracklist")
                        },
                        modifier = shapeModifier
                    )
                    is Artist -> ArtistItem(
                        artist = item,
                        onClick = {},
                        modifier = shapeModifier
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                if (query.text.isNotBlank()) {
                    LoadingIndicator()
                } else {
                    Text(
                        text = "输入搜索内容",
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(SmoothRoundedCornerShape(24.dp))
                            .background(99.n1 withNight 25.n1)
                            .padding(24.dp, 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
