package com.kyant.ncmapi

import com.kyant.ncmapi.data.Playlist
import com.kyant.ncmapi.utils.array
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.int
import com.kyant.ncmapi.utils.long

object ToplistApi {
    suspend fun getToplists(): List<Playlist> {
        return "https://music.163.com/api/toplist".apiGet {
            (it array "list").map { playlist ->
                Playlist(
                    id = playlist long "id",
                    name = playlist content "name",
                    imageUrl = playlist content "coverImgUrl",
                    trackCount = playlist int "trackCount"
                )
            }
        }
    }
}
