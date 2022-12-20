package com.kyant.ncmapi

import com.kyant.ncmapi.data.Album
import com.kyant.ncmapi.data.Artist
import com.kyant.ncmapi.data.Playlist
import com.kyant.ncmapi.data.Song
import com.kyant.ncmapi.utils.array
import com.kyant.ncmapi.utils.arrayContentList
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.int
import com.kyant.ncmapi.utils.long
import com.kyant.ncmapi.utils.obj
import com.kyant.ncmapi.utils.toHttps
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object PlaylistApi {
    suspend fun getDetail(
        id: Long?,
        recentSubscriberCount: Long = 5,
        cookie: String? = null
    ): Playlist {
        require(id != null) { "'id' cannot be null" }

        return "https://music.163.com/eapi/v6/playlist/detail".apiPost(
            buildJsonObject {
                put("id", id)
                put("n", 100000)
                put("s", recentSubscriberCount)
            },
            cookie = cookie
        ) {
            with(it obj "playlist") {
                Playlist(
                    id = this long "id",
                    name = this content "name",
                    imageUrl = this content "coverImgUrl",
                    trackCount = this int "trackCount",
                    tracks = (this array "tracks").map { song ->
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
                    },
                    trackIds = (this array "trackIds").map { id ->
                        id long "id"
                    }
                )
            }
        }
    }

    suspend fun subscribeOrUnsubscribe(
        id: Long?,
        subscribe: Boolean = true,
        cookie: String? = null
    ) {
        require(id != null) { "'id' cannot be null" }

        "https://music.163.com/eapi/playlist/${if (subscribe) "subscribe" else "unsubscribe"}".apiPost(
            buildJsonObject {
                put("id", id)
            },
            cookie = cookie
        ) {}
    }
}
