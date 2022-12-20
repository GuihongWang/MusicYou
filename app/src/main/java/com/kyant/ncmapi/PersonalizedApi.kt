package com.kyant.ncmapi

import com.kyant.ncmapi.data.Album
import com.kyant.ncmapi.data.Artist
import com.kyant.ncmapi.data.Song
import com.kyant.ncmapi.utils.array
import com.kyant.ncmapi.utils.arrayContentList
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.int
import com.kyant.ncmapi.utils.long
import com.kyant.ncmapi.utils.obj
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object PersonalizedApi {
    suspend fun getNewSongs(
        limit: Long = 20,
        areaId: Long = 0
    ): List<Song> {
        return "https://music.163.com/eapi/personalized/newsong".apiPost(
            buildJsonObject {
                put("type", "recommend")
                put("limit", limit)
                put("areaId", areaId)
            }
        ) {
            (it array "result").map { song ->
                with(song obj "song") {
                    Song(
                        id = song long "id",
                        name = song content "name",
                        artists = (this array "artists").map { artist ->
                            Artist(
                                id = artist long "id",
                                name = artist content "name"
                            )
                        },
                        alias = this arrayContentList "alias",
                        album = with(this obj "album") {
                            Album(
                                id = this long "id",
                                name = this content "name",
                                imageUrl = this content "picUrl"
                            )
                        },
                        duration = song long "dt",
                        fee = song int "fee"
                    )
                }
            }
        }
    }
}
