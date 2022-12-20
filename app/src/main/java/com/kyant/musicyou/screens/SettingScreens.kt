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
import com.kyant.musicyou.screens.settings.Home
import com.kyant.musicyou.screens.settings.Player
import com.kyant.musicyou.utils.animatedComposable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppViewModel.SettingScreens() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = 95.n2 withNight 10.n1
    ) {
        settingsNavController = rememberAnimatedNavController()
        AnimatedNavHost(
            navController = settingsNavController,
            startDestination = "home"
        ) {
            animatedComposable("home") { Home() }
            animatedComposable("player") { Player() }
        }
    }
}
