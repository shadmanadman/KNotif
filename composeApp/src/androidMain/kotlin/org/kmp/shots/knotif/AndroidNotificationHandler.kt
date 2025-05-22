package org.kmp.shots.knotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.kmp.shots.knotif.AndroidNotificationHandler.context

private const val NOTIFICATION_CHANNEL_ID = "knotif"
private const val NOTIFICATION_CHANNEL_NAME = "KNotif"

internal object AndroidNotificationHandler {

    private const val CHANNEL_ID = NOTIFICATION_CHANNEL_ID
    private const val CHANNEL_NAME = NOTIFICATION_CHANNEL_NAME

    private val context: Context by lazy { AppContext.get() }

    private val notificationManager: NotificationManager
        get() = ContextCompat.getSystemService(context, NotificationManager::class.java)!!

    private var onBuildMessageNotification: ((KNotifMessageData) -> Unit)? = null
    private var onBuildMusicNotification: ((KNotifMusicData) -> Unit)? = null
    private var onBuildProgressNotification: ((KNotifProgressData) -> Unit)? = null

    private var onPlayPauseClicked: (() -> Unit)? = null
    private var onNextClicked: (() -> Unit)? = null
    private var onPrevClicked: (() -> Unit)? = null

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
        var builder = when (notification) {
            is KNotifMessageData -> buildMessageNotification(notification)
            is KNotifMusicData -> buildMusicNotification(notification)
            is KNotifProgressData -> buildProgressNotification(notification)
        }

        builder = applyCallbacks(notification, builder)

        notificationManager.notify(notification.id.hashCode(), builder.build())
    }

    fun dismiss(notificationId: String) {
        notificationManager.cancel(notificationId.hashCode())
    }

    fun dismissAll() {
        notificationManager.cancelAll()
    }

    private fun buildBaseBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
    }

    private fun applyCallbacks(
        notification: KNotifData,
        builder: NotificationCompat.Builder
    ): NotificationCompat.Builder {
        when (notification) {
            is KNotifMessageData -> onBuildMessageNotification?.invoke(notification)
            is KNotifMusicData -> {
                onBuildMusicNotification?.invoke(notification)
                onPlayPauseClicked?.invoke()
                onNextClicked?.invoke()
                onPrevClicked?.invoke()
            }

            is KNotifProgressData -> onBuildProgressNotification?.invoke(notification)
        }
        return builder
    }


    fun setOnBuildMessageNotification(
        onKnotifClicked: (KNotifMessageData) -> Unit
    ) {
        onBuildMessageNotification = onKnotifClicked
    }

    fun setOnBuildMusicNotification(
        onKnotifClicked: (KNotifMusicData) -> Unit,
        playPauseClicked: () -> Unit,
        nextClicked: () -> Unit,
        previousClicked: () -> Unit
    ) {
        onBuildMusicNotification = onKnotifClicked
        onPlayPauseClicked = playPauseClicked
        onNextClicked = nextClicked
        onPrevClicked = previousClicked
    }

    fun setOnBuildProgressNotification(onKnotifClicked: (KNotifProgressData) -> Unit) {
        onBuildProgressNotification = onKnotifClicked
    }


    private fun buildMessageNotification(data: KNotifMessageData): NotificationCompat.Builder =
        buildBaseBuilder()
            .setCustomContentView(messageNotificationSmallView(context, data))
            .setCustomBigContentView(messageNotificationLargeView(context, data))
            .apply {
                data.appIcon?.let { setLargeIcon(it.asAndroidBitmap()) }
            }

    private fun buildMusicNotification(data: KNotifMusicData): NotificationCompat.Builder =
        buildBaseBuilder()
            .setCustomContentView(musicNotificationSmallView(context, data))
            .setCustomBigContentView(musicNotificationLargeView(context, data))
            .apply {
                data.appIcon?.let { setLargeIcon(it.asAndroidBitmap()) }
            }


    private fun buildProgressNotification(data: KNotifProgressData): NotificationCompat.Builder =
        buildBaseBuilder()
            .setCustomContentView(progressNotificationView(context, data))
            .apply {
                data.appIcon?.let { setLargeIcon(it.asAndroidBitmap()) }
            }
}

private fun musicNotificationSmallView(context: Context, data: KNotifMusicData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_music_small).apply {
        setTextViewText(R.id.knotif_music_title, data.title)
        setTextViewText(R.id.knotif_music_artist, data.artist)

        data.icons.previousIcon?.let {
            setImageViewBitmap(R.id.knotif_prev, it.asAndroidBitmap())
        }
        data.icons.nextIcon?.let {
            setImageViewBitmap(R.id.knotif_next, data.icons.nextIcon.asAndroidBitmap())
        }
        data.icons.playIcon.let {
            val playPauseIcon =
                if (data.isPlaying) data.icons.pauseIcon else data.icons.playIcon
            setImageViewBitmap(R.id.knotif_play_pause, playPauseIcon?.asAndroidBitmap())
        }
    }
}

private fun musicNotificationLargeView(context: Context, data: KNotifMusicData): RemoteViews {
    return RemoteViews(
        context.packageName,
        R.layout.knotif_music_large
    ).apply {
        setTextViewText(R.id.knotif_music_title, data.title)
        setTextViewText(R.id.knotif_music_artist, data.artist)

        data.icons.poster?.let {
            setImageViewBitmap(R.id.knotif_music_poster, it.asAndroidBitmap())
        }

        data.icons.previousIcon?.let {
            setImageViewBitmap(R.id.knotif_prev, it.asAndroidBitmap())
        }
        data.icons.nextIcon?.let {
            setImageViewBitmap(R.id.knotif_next, data.icons.nextIcon.asAndroidBitmap())
        }
        data.icons.playIcon.let {
            val playPauseIcon =
                if (data.isPlaying) data.icons.pauseIcon else data.icons.playIcon
            setImageViewBitmap(R.id.knotif_play_pause, playPauseIcon?.asAndroidBitmap())
        }
    }
}


private fun messageNotificationSmallView(context: Context, data: KNotifMessageData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_message_small).apply {
        setTextViewText(R.id.knotif_title, data.title)
    }
}


private fun messageNotificationLargeView(context: Context, data: KNotifMessageData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_message_large).apply {
        data.poster?.let { setImageViewBitmap(R.id.knotif_poster, it.asAndroidBitmap()) }
        setTextViewText(R.id.knotif_title, data.title)
        setTextViewText(R.id.knotif_message, data.message)
    }
}

private fun progressNotificationView(context: Context, data: KNotifProgressData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_progress_small).apply {
        setTextViewText(R.id.knotif_progress_title, data.title)
        setProgressBar(R.id.knotif_progress_bar, 100, data.progress, false)
    }
}