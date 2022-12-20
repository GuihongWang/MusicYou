package com.kyant.musicyou.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import com.kyant.musicyou.utils.asCatchableState
import com.kyant.musicyou.utils.launchedEffect
import com.kyant.ncmapi.PlaylistApi
import com.kyant.ncmapi.RecommendApi
import com.kyant.ncmapi.SearchApi
import com.kyant.ncmapi.SongApi
import com.kyant.ncmapi.ToplistApi
import com.kyant.ncmapi.UserApi
import com.kyant.ncmapi.data.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : PlayerViewModel(application) {
    lateinit var mainNavController: NavHostController
    lateinit var settingsNavController: NavHostController

    var isNowPlayingOpen by mutableStateOf(false)
    var isSearchOpen by mutableStateOf(false)
    var isMenuOpen by mutableStateOf(false)

    var query by mutableStateOf(TextFieldValue())

    var searchType by mutableStateOf(0)

    var searchSuggestions = snapshotFlow { query.text }.asCatchableState(
        SharingStarted.Lazily,
        emptyList()
    ) { query ->
        withContext(Dispatchers.IO) {
            if (query.isNotBlank()) {
                SearchApi.getSearchSuggestions(keywords = query.split(" "))
            } else emptyList()
        }
    }

    var searchResult = snapshotFlow { searchType to query.text }.asCatchableState(
        SharingStarted.Lazily,
        null
    ) { (type, query) ->
        withContext(Dispatchers.IO) {
            when (type) {
                0 -> SearchApi.searchSongs(keywords = query.split(" "))
                1 -> SearchApi.searchTracklists(keywords = query.split(" "))
                2 -> SearchApi.searchArtists(keywords = query.split(" "))
                else -> error("Invalid search type")
            }.also {
                if (query.isNotBlank()) {
                    it.load()
                }
            }
        }
    }

    var likelistIdsIncrement by mutableStateOf(0)

    var likelistIds = snapshotFlow { Triple(likelistIdsIncrement, profile?.id, login?.cookie) }.asCatchableState(
        SharingStarted.Eagerly,
        emptyList()
    ) { (_, uid, cookie) ->
        withContext(Dispatchers.IO) {
            uid?.let {
                SongApi.getUserLikelist(uid = it, cookie = cookie)
            } ?: emptyList()
        }
    }

    var myTracklistsIncrement by mutableStateOf(0)

    var myTracklists = snapshotFlow { Triple(myTracklistsIncrement, profile?.id, login?.cookie) }.asCatchableState(
        SharingStarted.Lazily,
        emptyList()
    ) { (_, uid, cookie) ->
        withContext(Dispatchers.IO) {
            uid?.let {
                UserApi.getUserPlaylists(uid = it, cookie = cookie)
            } ?: emptyList()
        }
    }

    val toplists = snapshotFlow {}.asCatchableState(
        SharingStarted.Lazily,
        emptyList()
    ) {
        withContext(Dispatchers.IO) {
            ToplistApi.getToplists()
        }
    }

    val recommendedSongs = snapshotFlow { login?.cookie }.asCatchableState(
        SharingStarted.Lazily,
        emptyList()
    ) { cookie ->
        withContext(Dispatchers.IO) {
            cookie?.let {
                RecommendApi.getSongs(cookie = it)
            } ?: emptyList()
        }
    }

    val recommendedTracklists = snapshotFlow { login?.cookie }.asCatchableState(
        SharingStarted.Lazily,
        emptyList()
    ) { cookie ->
        withContext(Dispatchers.IO) {
            cookie?.let {
                RecommendApi.getPlaylists(cookie = it)
            } ?: emptyList()
        }
    }

    var tracklist by mutableStateOf<Playlist?>(null)

    var isRankingTracklist by mutableStateOf(false)

    var tracklistSongs = snapshotFlow { tracklist?.id to login?.cookie }.asCatchableState(
        SharingStarted.Lazily,
        null
    ) { (id, cookie) ->
        withContext(Dispatchers.IO) {
            SongApi.getDetail(
                ids = PlaylistApi.getDetail(id = id, cookie = cookie).trackIds.toTypedArray()
            ).also {
                it.load()
            }
        }
    }

    suspend fun playTracklistSongs() {
        tracklistSongs.value?.loadAll(size = tracklist?.trackCount ?: 0)
        tracklistSongs.value?.data?.let {
            addPlaylistAndPlayFirst(it)
        }
    }

    suspend fun playRecommendedSongs() {
        addPlaylistAndPlayFirst(recommendedSongs.value)
    }

    init {
        launchedEffect({ searchType to query.text }) {
            searchResult.value?.data?.clear()
        }
        launchedEffect({ tracklist?.id }) {
            tracklistSongs.value?.data?.clear()
        }
    }
}
