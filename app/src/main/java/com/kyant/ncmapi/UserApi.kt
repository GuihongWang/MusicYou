package com.kyant.ncmapi

import com.kyant.ncmapi.data.Playlist
import com.kyant.ncmapi.data.UserLogin
import com.kyant.ncmapi.data.UserProfile
import com.kyant.ncmapi.utils.array
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.int
import com.kyant.ncmapi.utils.long
import com.kyant.ncmapi.utils.obj
import com.kyant.ncmapi.utils.toHttps
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object UserApi {
    suspend fun getUserProfile(login: UserLogin): UserProfile {
        return "https://music.163.com/eapi/nuser/account/get".apiPost(
            cookie = login.cookie
        ) {
            with(it obj "profile") {
                UserProfile(
                    id = this long "userId",
                    nickname = this content "nickname",
                    avatarUrl = (this content "avatarUrl").toHttps(),
                    backgroundUrl = (this content "backgroundUrl").toHttps()
                )
            }
        }
    }

    suspend fun getUserPlaylists(
        uid: Long?,
        limit: Long = 10000,
        offset: Long = 0,
        cookie: String? = null
    ): List<Playlist> {
        require(uid != null) { "'uid' cannot be null" }

        return "https://music.163.com/eapi/user/playlist".apiPost(
            buildJsonObject {
                put("uid", uid)
                put("limit", limit)
                put("offset", offset)
                put("includeVideo", true)
            },
            cookie = cookie
        ) {
            (it array "playlist").map { playlist ->
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
