package com.kyant.musicyou.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.kyant.musicyou.App
import java.io.File
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

inline fun <reified T> AndroidViewModel.mutableRecoverableStateOf(
    key: String,
    crossinline onEach: (T) -> Unit = {},
    crossinline value: () -> T = { null as T }
): MutableState<T> {
    val file = File(getApplication<App>().applicationContext.noBackupFilesDir.path, key)
    return mutableStateOf(
        file.takeIf { it.exists() }
            ?.readText()
            ?.let { Json.decodeFromString(it) }
            ?: value()
    ).apply {
        onEach(value())
    }
}
