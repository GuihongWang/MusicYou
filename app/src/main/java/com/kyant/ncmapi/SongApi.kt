package com.kyant.ncmapi

import com.kyant.ncmapi.data.Album
import com.kyant.ncmapi.data.Artist
import com.kyant.ncmapi.data.Song
import com.kyant.ncmapi.utils.PagingData
import com.kyant.ncmapi.utils.array
import com.kyant.ncmapi.utils.arrayContentList
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.int
import com.kyant.ncmapi.utils.long
import com.kyant.ncmapi.utils.obj
import com.kyant.ncmapi.utils.toHttps
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put

object SongApi {
    fun getDetail(
        vararg ids: Long?,
        limit: Int = 100,
        cookie: String? = null
    ) = PagingData(limit = limit) { page ->
        if (page * limit <= ids.size) {
            "https://music.163.com/eapi/v3/song/detail".apiPost(
                buildJsonObject {
                    put(
                        "c",
                        ids.toList()
                            .subList(page * limit, ((page + 1) * limit).coerceAtMost(ids.size))
                            .joinToString(prefix = "[", postfix = "]") {
                                Json.encodeToString(buildJsonObject { put("id", it) })
                            }
                    )
                },
                cookie = cookie
            ) {
                (it array "songs").map { song ->
                    Song(
                        id = song long "id",
                        name = song content "name",
                        artists = (song array "ar").map { artist ->
                            Artist(
                                id = artist long "id",
                                name = artist content "name"
                            )
                        },
                        alias = song arrayContentList "alia",
                        album = with(song obj "al") {
                            Album(
                                id = this long "id",
                                name = this content "name",
                                imageUrl = (this content "picUrl").toHttps()
                            )
                        },
                        duration = song long "dt",
                        fee = song int "fee"
                    )
                }
            }
        } else emptyList()
    }

    suspend fun getUserLikelist(
        uid: Long?,
        cookie: String? = null
    ): List<Long?> {
        require(uid != null) { "'uid' cannot be null" }

        return "https://music.163.com/eapi/song/like/get".apiPost(
            buildJsonObject {
                put("uid", uid)
            },
            cookie = cookie
        ) {
            (it array "ids").map { it.jsonPrimitive.longOrNull }.toList()
        }
    }

    suspend fun likeOrUnlike(
        id: Long?,
        like: Boolean = true,
        cookie: String? = null
    ) {
        require(id != null) { "'id' cannot be null" }

        "https://music.163.com/eapi/radio/like".apiPost(
            buildJsonObject {
                put("alg", "itembased")
                put("trackId", id)
                put("like", like)
                put("time", 3)
            },
            cookie = cookie
        ) {}
    }
}
