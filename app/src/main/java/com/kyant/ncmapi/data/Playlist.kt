package com.kyant.ncmapi.data

@kotlinx.serialization.Serializable
data class Playlist(
    val id: Long = 0,
    val name: String = "",
    val imageUrl: String = "",
    val trackCount: Int = 0,
    var tracks: List<Song> = emptyList(),
    var trackIds: List<Long> = emptyList()
)
