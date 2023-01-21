package com.kyant.ncmapi

import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.obj
import io.ktor.client.request.parameter

object LyricsApi {
    suspend fun getLyrics(id: Long?): String {
        require(id != null) { "'id' cannot be null" }

        return "https://music.163.com/api/song/lyric".apiGet(
            {
                parameter("id", id)
                parameter("_nmclfl", 1)
                parameter("tv", -1)
                parameter("lv", -1)
                parameter("rv", -1)
                parameter("kv", -1)
            },
            cookie = "os=pc"
        ) {
            it obj "lrc" content "lyric"
        }
    }
}
