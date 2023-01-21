package com.kyant.ncmapi

import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.obj
import io.ktor.client.request.parameter

object LyricsApi {
    suspend fun getLyrics(id: Long?): String {
        require(id != null) { "'id' cannot be null" }

        return "https://music.163.com/api/song/lyric".apiGet(
            {
                parameter("os", "uwp")
                parameter("id", id)
                parameter("lv", -1)
                parameter("kv", -1)
                parameter("tv", -1)
            },
        ) {
            it obj "lrc" content "lyric"
        }
    }
}
