package com.kyant.ncmapi.data

@kotlinx.serialization.Serializable
data class Artist(
    val id: Long = 0,
    val name: String = "",
    val imageUrl: String = ""
)
