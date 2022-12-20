package com.kyant.ncmapi.data

@kotlinx.serialization.Serializable
data class SongUrl(
    val id: Long = 0,
    val url: String = "",
    val size: Long = 0
)
