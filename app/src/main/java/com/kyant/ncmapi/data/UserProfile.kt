package com.kyant.ncmapi.data

@kotlinx.serialization.Serializable
data class UserProfile(
    val id: Long = 0,
    val nickname: String = "",
    val avatarUrl: String = "",
    val backgroundUrl: String = ""
)
