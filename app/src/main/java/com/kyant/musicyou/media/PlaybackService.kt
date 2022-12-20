package com.kyant.musicyou.media

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import java.io.File

class PlaybackService : MediaSessionService() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createMediaStyleNotification(PlayerEssentials.mediaSession)
        startForeground(1, notification)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return PlayerEssentials.mediaSession
    }

    override fun onDestroy() {
        File(noBackupFilesDir.path, "synced").writeText(false.toString())
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        super.onDestroy()
    }
}
