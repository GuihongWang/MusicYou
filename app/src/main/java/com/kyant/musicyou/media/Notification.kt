package com.kyant.musicyou.media

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.kyant.musicyou.R

fun Context.createNotificationChannel() {
    val manager = getSystemService(NotificationManager::class.java)
    val notificationChannel = NotificationChannel(
        "music",
        "Music",
        NotificationManager.IMPORTANCE_NONE
    ).apply {
        description = "Music"
        enableVibration(false)
    }
    manager.createNotificationChannel(notificationChannel)
}

@SuppressLint("UnsafeOptInUsageError")
fun Context.createMediaStyleNotification(mediaSession: MediaSession): Notification {
    val mediaStyle = MediaStyleNotificationHelper.MediaStyle(mediaSession)
        .setShowCancelButton(true)
        .setCancelButtonIntent(
            MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)
        )
        .setShowActionsInCompactView(0, 1, 2)
    return androidx.core.app.NotificationCompat.Builder(this, "music")
        .setStyle(mediaStyle)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setDeleteIntent(
            MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)
        )
        .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
        .setSilent(true)
        .build()
}
