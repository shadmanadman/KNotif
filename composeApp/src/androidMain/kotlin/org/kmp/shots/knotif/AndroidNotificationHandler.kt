package org.kmp.shots.knotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import kotlin.text.get

private const val NOTIFICATION_CHANNEL_ID = "knotif"
private const val NOTIFICATION_CHANNEL_NAME = "KNotif"

internal object AndroidNotificationHandler {

    private const val CHANNEL_ID = NOTIFICATION_CHANNEL_ID
    private const val CHANNEL_NAME = NOTIFICATION_CHANNEL_NAME

    private val context: Context by lazy { AppContext.get() }

    private val notificationManager: NotificationManager
        get() = ContextCompat.getSystemService(context, NotificationManager::class.java)!!

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "KNotif channel"
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun show(notification: KNotifData) {
        val builder = when (notification) {
            is KNotifMessageData -> buildMessageNotification(notification)
            is KNotifMusicData -> buildMusicNotification(notification)
            is KNotifProgressData -> buildProgressNotification(notification)
        }

        notificationManager.notify(notification.id.hashCode(), builder.build())
    }

    fun dismiss(notificationId: String) {
        notificationManager.cancel(notificationId.hashCode())
    }

    fun dismissAll() {
        notificationManager.cancelAll()
    }

    private fun buildBaseBuilder(data: KNotifData): NotificationCompat.Builder {
        val style = data.style
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(data.title)
            .setSmallIcon(getIconResource(data.appIcon.toString()))
            .setColor(style.backgroundColor.toColorInt())
            .setAutoCancel(true)
    }

    private fun buildMessageNotification(data: KNotifMessageData): NotificationCompat.Builder {
        return buildBaseBuilder(data)
            .setContentText(data.message)
    }



    private fun buildMusicNotification(data: KNotifMusicData): NotificationCompat.Builder {
        val playPauseIcon = if (data.isPlaying) data.icons.playIcon else data.icons.pauseIcon
        return buildBaseBuilder(data)
            .setContentText("${data.title} — ${data.artist}")
            .addAction(getIconResource(data.icons.previousIcon), "Back", null)
            .addAction(getIconResource(playPauseIcon), if (data.isPlaying) "Pause" else "Play", null)
            .addAction(getIconResource(data.icons.nextIcon), "Next", null)
    }

    private fun buildProgressNotification(data: KNotifProgressData): NotificationCompat.Builder {
        return buildBaseBuilder(data)
            .setProgress(100, data.progress, false)
            .setContentText("${data.progress}%")
    }

    private fun getIconResource(iconName: String?): Int {
        return if (iconName != null) {
            context.resources.getIdentifier(iconName, "drawable", context.packageName)
        } else {
            android.R.drawable.ic_dialog_info // fallback
        }
    }
}