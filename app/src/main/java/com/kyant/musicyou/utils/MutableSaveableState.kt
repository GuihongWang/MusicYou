package com.kyant.musicyou.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kyant.musicyou.App
import java.io.File
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> AndroidViewModel.mutableSaveableStateOf(
    key: String,
    noinline onEach: suspend (T) -> Unit = {},
    crossinline value: () -> T = { null as T }
): MutableState<T> {
    val file = File(getApplication<App>().applicationContext.noBackupFilesDir.path, key)
    return mutableStateOf(
        file.takeIf { it.exists() }
            ?.readText()
            ?.let { Json.decodeFromString(it) }
            ?: value()
    ).apply {
        snapshotFlow { this.value }
            .onEach {
                file.writeText(Json.encodeToString(it))
                // println("Value saved, $key: $it")
                onEach(it)
            }
            .launchIn(viewModelScope)
    }
}

inline fun <reified T> AndroidViewModel.mutableSaveableStateListOf(
    key: String,
    noinline onEach: suspend (List<T>) -> Unit = {},
    crossinline value: () -> List<T> = { emptyList() }
): SnapshotStateList<T> {
    val file = File(getApplication<App>().applicationContext.noBackupFilesDir.path, key)
    return mutableStateListOf(
        *(
            file.takeIf { it.exists() }
                ?.readText()
                ?.let { Json.decodeFromString<List<T>>(it) }
                ?: value()
            ).toTypedArray()
    ).apply {
        snapshotFlow { toList() }
            .onEach {
                file.writeText(Json.encodeToString(it))
                // println("Value saved, $key: $it")
                onEach(it)
            }
            .launchIn(viewModelScope)
    }
}
