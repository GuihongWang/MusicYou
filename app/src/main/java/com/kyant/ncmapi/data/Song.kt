package com.kyant.ncmapi.data

@kotlinx.serialization.Serializable
data class Song(
    val id: Long = 0,
    val name: String = "",
    val artists: List<Artist> = emptyList(),
    val alias: List<String> = emptyList(),
    val album: Album = Album(),
    val duration: Long = 0,
    /**
     * fee: enum,
     0: 免费或无版权
     1: VIP 歌曲
     4: 购买专辑
     8: 非会员可免费播放低音质，会员可播放高音质及下载
     fee 为 1 或 8 的歌曲均可单独购买 2 元单曲
     */
    val fee: Int = 0,
    val url: String = ""
)
