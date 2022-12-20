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

object RecommendApi {
    suspend fun getSongs(cookie: String? = null): List<Song> {
        return "https://music.163.com/api/v3/discovery/recommend/songs".apiPost(
            buildJsonObject {},
            cookie = cookie
        ) {
            (it obj "data" array "dailySongs").map { song ->
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

    suspend fun getPlaylists(cookie: String? = null): List<Playlist> {
        return "https://music.163.com/api/v1/discovery/recommend/resource".apiPost(
            buildJsonObject {},
            cookie = cookie
        ) {
            (it array "recommend").map { playlist ->
                Playlist(
                    id = playlist long "id",
                    name = playlist content "name",
                    imageUrl = playlist content "picUrl",
                    trackCount = playlist int "trackCount"
                )
            }
        }
    }
}
