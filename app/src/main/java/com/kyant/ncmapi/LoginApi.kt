package com.kyant.ncmapi

import com.kyant.ncmapi.data.UserLogin
import com.kyant.ncmapi.utils.content
import com.kyant.ncmapi.utils.long
import com.kyant.ncmapi.utils.obj
import com.kyant.ncmapi.utils.toMD5Hex
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object LoginApi {
    suspend fun phoneNumberLogIn(
        phone: Long?,
        password: String,
        countryCode: Int? = null
    ): UserLogin {
        require(phone != null) { "'phone' cannot be null" }
        require(password.isNotBlank()) { "'password' cannot be blank" }

        return "https://music.163.com/eapi/login/cellphone".apiPostWithCookieReturned(
            buildJsonObject {
                put("phone", phone)
                put("countrycode", countryCode ?: 86)
                put("password", password.toMD5Hex())
                put("rememberLogin", true)
            }
        ) { response, cookie ->
            UserLogin(
                id = response obj "account" long "id",
                name = response obj "account" content "userName",
                token = response content "token",
                avatarUrl = response obj "profile" content "avatarUrl",
                backgroundUrl = response obj "profile" content "backgroundUrl",
                cookie = cookie
            )
        }
    }

    suspend fun emailLogIn(
        email: String,
        password: String
    ): UserLogin {
        require(email.isNotBlank()) { "'email' cannot be blank" }
        require(password.isNotBlank()) { "'password' cannot be blank" }

        return "https://music.163.com/eapi/login".apiPostWithCookieReturned(
            buildJsonObject {
                put("username", email)
                put("password", password.toMD5Hex())
                put("rememberLogin", true)
            }
        ) { response, cookie ->
            UserLogin(
                id = response obj "account" long "id",
                name = response obj "account" content "userName",
                token = response content "token",
                avatarUrl = response obj "profile" content "avatarUrl",
                backgroundUrl = response obj "profile" content "backgroundUrl",
                cookie = cookie
            )
        }
    }
}
