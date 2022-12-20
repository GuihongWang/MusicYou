package com.kyant.musicyou.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kyant.musicyou.App
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

suspend inline fun <reified T> Context.withException(content: Context.() -> T) {
    try {
        content()
    } catch (_: CancellationException) {
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                this@withException,
                e.message ?: e.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

context (AndroidViewModel)
@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T, R> Flow<T>.asCatchableState(
    started: SharingStarted,
    initialValue: R,
    crossinline transform: suspend (value: T) -> R
): StateFlow<R> = mapLatest {
    try {
        transform(it)
    } catch (_: CancellationException) {
        initialValue
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                getApplication<App>().applicationContext,
                e.message ?: e.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
        initialValue
    }
}.stateIn(viewModelScope, started, initialValue)
