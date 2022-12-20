package com.kyant.ncmapi

import com.kyant.ncmapi.data.SongUrl
import com.kyant.ncmapi.utils.PagingData
import com.kyant.ncmapi.utils.array
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.long
import com.kyant.ncmapi.utils.toHttps
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object PlayerApi {
    suspend fun getSongUrl(
        id: Long?,
        cookie: String? = null
    ): SongUrl {
        require(id != null) { "'id' cannot be null" }

        return "https://interface3.music.163.com/eapi/song/enhance/player/url".apiPost(
            buildJsonObject {
                put("ids", "[$id]")
                put("br", 999000)
            },
            cookie = cookie
        ) {
            (it array "data").map { data ->
                SongUrl(
                    id = data long "id",
                    url = (data content "url").toHttps(),
                    size = data long "size"
                )
            }.first()
        }
    }

    fun getSongUrls(
        vararg ids: Long?,
        limit: Int = 50,
        cookie: String? = null
    ) = PagingData(limit = limit) { page ->
        if (page * limit <= ids.size) {
            "https://interface3.music.163.com/eapi/song/enhance/player/url".apiPost(
                buildJsonObject {
                    put(
                        "ids",
                        ids.toList()
                            .subList(page * limit, ((page + 1) * limit).coerceAtMost(ids.size))
                            .joinToString(prefix = "[", postfix = "]")
                    )
                    put("br", 999000)
                },
                cookie = cookie
            ) {
                (it array "data").map { data ->
                    SongUrl(
                        id = data long "id",
                        url = (data content "url").toHttps(),
                        size = data long "size"
                    )
                }
            }
        } else emptyList()
    }
}
