package com.kyant.ncmapi.data

@kotlinx.serialization.Serializable
data class UserLogin(
    val id: Long = 0,
    val name: String = "",
    val token: String = "",
    val avatarUrl: String = "",
    val backgroundUrl: String = "",
    val cookie: String = ""
)
