package com.kyant.musicyou.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.google.accompanist.navigation.animation.composable
import com.kyant.musicyou.App

@OptIn(ExperimentalAnimationApi::class)
fun AndroidViewModel.createNavController() = NavHostController(
    getApplication<App>().applicationContext
).apply {
    with(navigatorProvider) {
        addNavigator(AnimatedComposeNavigator())
        addNavigator(ComposeNavigator())
        addNavigator(DialogNavigator())
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))
    },
    exitTransition = {
        fadeOut(animationSpec = tween(90, delayMillis = 90)) +
            scaleOut(targetScale = 0.92f, animationSpec = tween(90, delayMillis = 90))
    },
    popEnterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))
    },
    popExitTransition = {
        fadeOut(animationSpec = tween(90, delayMillis = 90)) +
            scaleOut(targetScale = 0.92f, animationSpec = tween(90, delayMillis = 90))
    },
    content = content
)
