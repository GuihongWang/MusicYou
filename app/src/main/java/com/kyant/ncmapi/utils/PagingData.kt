package com.kyant.ncmapi.utils

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class PagingData<T>(
    private val limit: Int,
    private val onLoad: suspend (Int) -> List<T>
) {
    private var page = 0
    val data = mutableStateListOf<T>()

    suspend fun load() {
        data += onLoad(page)
        page++
    }

    suspend fun loadAll(size: Int) {
        withContext(Dispatchers.IO) {
            (page..size / limit + 1).map {
                async {
                    page++
                    onLoad(it)
                }
            }.awaitAll().map { data += it }
        }
    }
}
