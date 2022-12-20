package com.kyant.musicyou.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
inline fun <T> rememberLazyLaunchIn(
    context: CoroutineContext,
    initialValue: T? = null,
    crossinline block: suspend () -> T?
): T? {
    var result: T? by remember { mutableStateOf(initialValue) }
    LaunchedEffect(Unit) {
        withContext(context) {
            result = block()
        }
    }
    return result
}

inline fun <T> ViewModel.lazyLaunchIn(
    context: CoroutineContext,
    initialValue: T? = null,
    crossinline block: suspend () -> T?
): T? {
    var result: T? by mutableStateOf(initialValue)
    viewModelScope.launch(context) {
        result = block()
    }
    return result
}

inline fun <T> ViewModel.launchedEffect(
    crossinline key: () -> T,
    crossinline block: suspend CoroutineScope.(T) -> Unit
) {
    snapshotFlow { key() }
        .onEach { block(viewModelScope, it) }
        .launchIn(viewModelScope)
}

context (ViewModel)
inline fun <T> MutableState<T>.withLaunchedEffect(
    crossinline block: suspend CoroutineScope.(T) -> Unit
): MutableState<T> = apply {
    snapshotFlow { value }
        .onEach { block(viewModelScope, it) }
        .launchIn(viewModelScope)
}
