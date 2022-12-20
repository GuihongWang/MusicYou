package com.kyant.ncmapi

import com.kyant.ncmapi.data.Album
import com.kyant.ncmapi.data.Artist
import com.kyant.ncmapi.data.Playlist
import com.kyant.ncmapi.data.Song
import com.kyant.ncmapi.utils.PagingData
import com.kyant.ncmapi.utils.array
import com.kyant.ncmapi.utils.arrayContentList
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.int
import com.kyant.ncmapi.utils.long
import com.kyant.ncmapi.utils.obj
import com.kyant.ncmapi.utils.toHttps
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object SearchApi {
    suspend fun getSearchSuggestions(keywords: List<String>): List<String> {
        require(keywords.isNotEmpty()) { "'keywords' cannot be empty" }

        return "https://music.163.com/eapi/search/suggest/keyword".apiPost(
            buildJsonObject {
                put("s", keywords.joinToString(" "))
            }
        ) {
            (it obj "result" array "allMatch").map { match -> match content "keyword" }
        }
    }

    fun searchSongs(
        keywords: List<String>,
        limit: Int = 20
    ) = PagingData(limit = limit) { page ->
        "https://music.163.com/eapi/cloudsearch/pc".apiPost(
            searchJsonObject(
                keywords,
                limit,
                page * limit,
                type = 1
            )
        ) {
            (it obj "result" array "songs").map { song ->
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
    }

    fun searchTracklists(
        keywords: List<String>,
        limit: Int = 20
    ) = PagingData(limit = limit) { page ->
        "https://music.163.com/eapi/cloudsearch/pc".apiPost(
            searchJsonObject(
                keywords,
                limit,
                page * limit,
                type = 1000
            )
        ) {
            (it obj "result" array "playlists").map { playlist ->
                Playlist(
                    id = playlist long "id",
                    name = playlist content "name",
                    imageUrl = playlist content "coverImgUrl",
                    trackCount = playlist int "trackCount"
                )
            }
        }
    }

    fun searchArtists(
        keywords: List<String>,
        limit: Int = 20
    ) = PagingData(limit = limit) { page ->
        "https://music.163.com/eapi/cloudsearch/pc".apiPost(
            searchJsonObject(
                keywords,
                limit,
                page * limit,
                type = 100
            )
        ) {
            (it obj "result" array "artists").map { artist ->
                Artist(
                    id = artist long "id",
                    name = artist content "name",
                    imageUrl = artist content "picUrl"
                )
            }
        }
    }

    private fun searchJsonObject(
        keywords: List<String>,
        limit: Int = 20,
        offset: Int = 0,
        type: Long // 1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频
    ): JsonObject {
        require(keywords.isNotEmpty()) { "'keywords' cannot be empty" }

        return buildJsonObject {
            put("s", keywords.joinToString(" "))
            put("limit", limit)
            put("offset", offset)
            put("type", type)
            put("total", true)
        }
    }
}
