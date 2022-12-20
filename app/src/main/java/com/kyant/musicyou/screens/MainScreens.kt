package com.kyant.musicyou.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kyant.monet.n1
import com.kyant.monet.n2
import com.kyant.monet.withNight
import com.kyant.musicyou.data.AppViewModel
import com.kyant.musicyou.screens.main.Home
import com.kyant.musicyou.screens.main.Lyrics
import com.kyant.musicyou.screens.main.NowPlaying
import com.kyant.musicyou.screens.main.Playlists
import com.kyant.musicyou.screens.main.SongInfo
import com.kyant.musicyou.screens.main.Tracklist
import com.kyant.musicyou.screens.main.TracklistSongs
import com.kyant.musicyou.utils.animatedComposable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppViewModel.MainScreens() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = 95.n2 withNight 10.n1
    ) {
        mainNavController = rememberAnimatedNavController()
        AnimatedNavHost(
            navController = mainNavController,
            startDestination = "home"
        ) {
            animatedComposable("home") { Home() }
            animatedComposable("now_playing") { NowPlaying() }
            animatedComposable("lyrics") { Lyrics() }
            animatedComposable("song_info") { SongInfo() }
            animatedComposable("playlists") { Playlists() }
            animatedComposable("tracklist") { Tracklist() }
            animatedComposable("tracklist_songs") { TracklistSongs() }
        }
    }
}
