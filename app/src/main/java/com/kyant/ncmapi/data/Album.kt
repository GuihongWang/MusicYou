package com.kyant.ncmapi.data

@kotlinx.serialization.Serializable
data class Album(
    val id: Long = 0,
    val name: String = "",
    val imageUrl: String = ""
)
