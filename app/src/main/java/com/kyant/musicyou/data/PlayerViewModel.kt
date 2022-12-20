package com.kyant.musicyou.data

import StatusBarLyric.API.StatusBarLyric
import android.annotation.SuppressLint
import android.app.Application
import android.app.WallpaperColors
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import coil.ImageLoader
import coil.request.ImageRequest
import com.kyant.musicyou.R
import com.kyant.musicyou.data.KeyColor.Companion.toKeyColor
import com.kyant.musicyou.media.PlaybackService
import com.kyant.musicyou.media.PlayerEssentials
import com.kyant.musicyou.media.currentLyricOrNull
import com.kyant.musicyou.media.toLyrics
import com.kyant.musicyou.utils.launchedEffect
import com.kyant.musicyou.utils.mutableSaveableStateListOf
import com.kyant.musicyou.utils.mutableSaveableStateOf
import com.kyant.musicyou.utils.withException
import com.kyant.ncmapi.LyricsApi
import com.kyant.ncmapi.PlayerApi
import com.kyant.ncmapi.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class PlayerViewModel(application: Application) : UserViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private var synced by mutableStateOf(false)

    private val statusBarLyric = StatusBarLyric(
        context,
        context.getDrawable(R.drawable.ic_launcher_bitmap),
        "com.kyant.musicyou.media.PlaybackService",
        false
    ).takeIf { it.hasEnable() }

    var isPlaying by mutableSaveableStateOf("isPlaying", {
        if (it) {
            context.startForegroundService(Intent(context, PlaybackService::class.java))
            try {
                currentLyricOrNull()?.let {
                    statusBarLyric?.updateLyric(it)
                }
            } catch (_: NullPointerException) {
            }
        } else {
            statusBarLyric?.stopLyric()
        }
    }) { false }
    var isBuffering by mutableSaveableStateOf("isBuffering") { false }

    var shuffleMode by mutableSaveableStateOf("shuffleMode") { false }
    var repeatMode by mutableSaveableStateOf("repeatMode") { Player.REPEAT_MODE_OFF }

    val mainPlaylist = mutableSaveableStateListOf<Song>("mainPlaylist")
    val playlists = mutableSaveableStateListOf<List<Song>>("playlists")
    var currentPlaylistIndex by mutableSaveableStateOf("currentPlaylistIndex") { -1 }
    inline val currentPlaylist
        get() = if (currentPlaylistIndex == -1) mainPlaylist
        else playlists.getOrElse(currentPlaylistIndex) { emptyList() }
    private inline val isCurrentMainPlaylist
        get() = currentPlaylistIndex == -1

    var currentSongIndex by mutableSaveableStateOf("currentSongIndex") { 0 }
    inline val currentSong: Song?
        get() = currentPlaylist.getOrNull(currentSongIndex)
    var hasNextSong by mutableSaveableStateOf("hasNextSong") { true }

    inline val currentDuration
        get() = currentSong?.duration ?: 0L
    var currentPosition by mutableSaveableStateOf("currentPosition") { 0L }
    inline val currentPositionPercentage: Float
        get() = if (currentDuration == 0L) 0f
        else currentPosition.toFloat() / currentDuration.toFloat()

    var preparedSeekingPosition: Long? by mutableSaveableStateOf("preparedSeekingPosition")

    var currentLyrics by mutableSaveableStateOf("currentLyrics") { emptyMap<String, String>() }

    private val imageLoader = ImageLoader(context).newBuilder()
        .allowHardware(false)
        .build()

    var keyColor by mutableSaveableStateOf("keyColor") {
        KeyColor(0f, 127f / 255, 172f / 255)
    }

    init {
        // PlayerEssentials.player.experimentalSetOffloadSchedulingEnabled(true)
        PlayerEssentials.player.shuffleModeEnabled = shuffleMode
        PlayerEssentials.player.repeatMode = repeatMode
        viewModelScope.launch(Dispatchers.IO) {
            awaitFrame()
            val items = currentPlaylist.toMediaItems()
            withContext(Dispatchers.Main) {
                PlayerEssentials.player.setMediaItems(items)
                seekToSong(currentSongIndex, currentPosition)
                if (isPlaying) play() else pause()
                PlayerEssentials.player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        updateBufferingState()
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        updatePlayingState()
                        updateCurrentPosition()
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        updateCurrentSongIndex()
                        updateCurrentPosition()
                    }

                    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                        super.onTimelineChanged(timeline, reason)
                        if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                            updateCurrentSongIndex()
                        }
                    }
                })
                synced = true
            }
        }
        viewModelScope.launch {
            while (true) {
                if (synced) {
                    updateCurrentPosition()
                }
                delay(50)
            }
        }
        launchedEffect({ currentSong }) { song ->
            if (song != null) {
                currentLyrics = emptyMap()
                statusBarLyric?.stopLyric()
                withContext(Dispatchers.IO) {
                    context.withException {
                        currentLyrics = LyricsApi.getLyrics(id = song.id).toLyrics().filterNot { (_, lyric) ->
                            lyric.startsWith(" 作词 : ") ||
                                lyric.startsWith(" 作曲 : ") ||
                                lyric.startsWith(" 编曲 : ") ||
                                lyric.startsWith(" 制作人 : ")
                        }
                    }
                }
                withContext(Dispatchers.IO) {
                    imageLoader.execute(
                        ImageRequest.Builder(context)
                            .data("${song.album.imageUrl}?param=24y24")
                            .build()
                    ).drawable?.toBitmap()?.let {
                        keyColor = Color(WallpaperColors.fromBitmap(it).primaryColor.toArgb()).toKeyColor()
                    }
                }
            } else {
                currentLyrics = emptyMap()
            }
        }
        launchedEffect({ currentLyricOrNull() }) {
            it?.let {
                statusBarLyric?.updateLyric(it)
            }
        }
    }

    private fun play() {
        PlayerEssentials.player.prepare()
        PlayerEssentials.player.play()
    }

    private fun pause() {
        PlayerEssentials.player.pause()
    }

    fun playOrPause() {
        if (isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun updatePlayingState() {
        isPlaying = PlayerEssentials.player.isPlaying
    }

    fun updateBufferingState() {
        isBuffering = PlayerEssentials.player.playbackState == ExoPlayer.STATE_BUFFERING
    }

    fun updateCurrentSongIndex() {
        currentSongIndex = PlayerEssentials.player.currentMediaItemIndex
        hasNextSong = PlayerEssentials.player.hasNextMediaItem()
    }

    fun updateCurrentPosition() {
        currentPosition = PlayerEssentials.player.currentPosition
    }

    fun changeShuffleMode() {
        shuffleMode = !shuffleMode
        PlayerEssentials.player.shuffleModeEnabled = shuffleMode
    }

    fun changeRepeatMode() {
        val mode = when (repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> error("Invalid repeat mode")
        }
        repeatMode = mode
        PlayerEssentials.player.repeatMode = mode
    }

    fun seekTo(position: Long) {
        PlayerEssentials.player.seekTo(position)
    }

    fun seekToPercentage(percentage: Float) {
        seekTo((percentage * PlayerEssentials.player.duration).toLong())
    }

    suspend fun seekToPlaylist(index: Int) {
        if (currentPlaylistIndex != index) {
            currentPlaylistIndex = index
            currentSongIndex = 0
            PlayerEssentials.player.setMediaItems(currentPlaylist.toMediaItems())
        }
    }

    fun seekToSong(index: Int, position: Long = 0) {
        PlayerEssentials.player.seekTo(index, position)
    }

    fun seekToSongAndPlay(index: Int, position: Long = 0) {
        seekToSong(index, position)
        play()
    }

    fun seekToPreviousSong() {
        if (currentSongIndex != 0 ||
            (currentSongIndex == 0 && (shuffleMode || repeatMode != Player.REPEAT_MODE_OFF))
        ) {
            PlayerEssentials.player.seekToPreviousMediaItem()
        } else {
            seekTo(0)
        }
    }

    fun seekToNextSong() {
        PlayerEssentials.player.seekToNextMediaItem()
    }

    private suspend fun Song.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(name)
                    .setArtist(artists.joinToString { it.name })
                    .setArtworkUri(album.imageUrl.toUri())
                    .build()
            )
            .setUri(
                PlayerApi.getSongUrl(
                    id = id,
                    cookie = login?.cookie
                ).url
            )
            .build()
    }

    private suspend fun List<Song>.toMediaItems(): List<MediaItem> {
        val urls = PlayerApi.getSongUrls(
            ids = map { it.id }.toTypedArray(),
            cookie = login?.cookie
        )
        urls.loadAll(size = size)
        val sortedUrls = urls.data.sortedBy { url ->
            this
                .map { song -> song.id }
                .indexOf(url.id)
        }
        return zip(sortedUrls).map { (song, url) ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.name)
                        .setArtist(song.artists.joinToString { it.name })
                        .setArtworkUri(song.album.imageUrl.toUri())
                        .build()
                )
                .setUri(url.url)
                .build()
        }
    }

    suspend fun addToMainPlaylistAndPlay(song: Song) {
        if (song.id !in mainPlaylist.map { it.id }) {
            mainPlaylist += song
            PlayerEssentials.player.addMediaItem(song.toMediaItem())
        }
        withContext(Dispatchers.Main) {
            seekToPlaylist(-1)
            seekToSongAndPlay(mainPlaylist.indexOfFirst { it.id == song.id })
        }
    }

    suspend fun addPlaylistAndPlayFirst(songs: List<Song>) {
        playlists += songs
        withContext(Dispatchers.Main) {
            seekToPlaylist(playlists.lastIndex)
            seekToSongAndPlay(0)
        }
    }

    fun removeFromMainPlaylist(song: Song) {
        val index = mainPlaylist.indexOf(song)
        if (index == mainPlaylist.lastIndex && song == currentSong) {
            mainPlaylist -= song
            if (isCurrentMainPlaylist) {
                PlayerEssentials.player.removeMediaItem(index)
                PlayerEssentials.player.seekTo((index - 1).coerceAtLeast(0), 0)
            }
        } else {
            mainPlaylist -= song
            if (isCurrentMainPlaylist) {
                PlayerEssentials.player.removeMediaItem(index)
            }
        }
    }

    context (Context)
    fun clearMainPlaylist() {
        if (isCurrentMainPlaylist) {
            PlayerEssentials.player.clearMediaItems()
        }
        mainPlaylist.clear()
    }

    context (Context)
    suspend fun removePlaylistAt(index: Int) {
        when {
            index == currentPlaylistIndex -> {
                PlayerEssentials.player.clearMediaItems()
                playlists.removeAt(index)
                seekToPlaylist(index - 1)
            }
            index < currentPlaylistIndex -> {
                playlists.removeAt(index)
                currentPlaylistIndex--
            }
            else -> {
                playlists.removeAt(index)
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class KeyColor(
    val r: Float,
    val g: Float,
    val b: Float
) {
    fun toColor() = Color(r, g, b)

    companion object {
        fun Color.toKeyColor() = KeyColor(red, green, blue)
    }
}
