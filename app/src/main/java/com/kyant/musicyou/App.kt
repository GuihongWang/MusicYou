package com.kyant.musicyou

import android.app.Application
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.kyant.musicyou.media.PlayerEssentials

class App : Application(), ViewModelStoreOwner {
    private val viewModelStore = ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        PlayerEssentials.player = ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()
        PlayerEssentials.mediaSession = MediaSession.Builder(applicationContext, PlayerEssentials.player)
            .setSessionActivity(
                TaskStackBuilder.create(applicationContext).run {
                    addNextIntent(Intent(applicationContext, MainActivity::class.java))
                    getPendingIntent(
                        0,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            )
            .build()
    }

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }
}
