package org.kmp.shots.knotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import org.jetbrains.compose.resources.DrawableResource

private const val NOTIFICATION_CHANNEL_ID = "knotif"
private const val NOTIFICATION_CHANNEL_NAME = "KNotif"

const val ACTION_PREVIOUS = "knotif.ACTION_PREVIOUS"
const val ACTION_NEXT = "knotif.ACTION_NEXT"
const val ACTION_PLAY_PAUSE = "knotif.ACTION_PLAY_PAUSE"
const val ACTION_NOTIFICATION_CLICK = "knotif.ACTION_NOTIFICATION_CLICK"

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

    private val notificationDataCache = mutableMapOf<String, KNotifData>()

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
        notificationDataCache[notification.id] = notification
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

    private fun buildBaseBuilder(appIcon: ImageBitmap): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setAutoCancel(true).apply {
                appIcon.let {
                    setSmallIcon(appIcon.toIconCompat())
                    setLargeIcon(appIcon.asAndroidBitmap())
                }
            }
    }

    fun getNotificationData(id: String): KNotifData? = notificationDataCache[id]


    private fun applyCallbacks(
        notification: KNotifData,
        builder: NotificationCompat.Builder
    ): NotificationCompat.Builder {
        when (notification) {
            is KNotifMessageData -> handleMessageClicked(notification)
            is KNotifMusicData -> {
                handleMusicClicked(notification)
                handlePlayPauseClicked()
                handleNextClicked()
                handlePrevClicked()
            }

            is KNotifProgressData -> handleProgressClicked(notification)
        }
        return builder
    }

    fun handlePlayPauseClicked() {
        onPlayPauseClicked?.invoke()
    }

    fun handleNextClicked() {
        onNextClicked?.invoke()
    }

    fun handlePrevClicked() {
        onPrevClicked?.invoke()
    }

    fun handleMessageClicked(notification: KNotifMessageData) {
        onBuildMessageNotification?.invoke(notification)
    }

    fun handleMusicClicked(notification: KNotifMusicData) {
        onBuildMusicNotification?.invoke(notification)
    }

    fun handleProgressClicked(notification: KNotifProgressData) {
        onBuildProgressNotification?.invoke(notification)
    }

    fun setOnBuildMessageKnotifListener(
        knotifClicked: (KNotifMessageData) -> Unit
    ) {
        onBuildMessageNotification = knotifClicked
    }

    fun setOnBuildMusicKnotifListener(
        knotifClicked: (KNotifMusicData) -> Unit,
        playPauseClicked: () -> Unit,
        nextClicked: () -> Unit,
        previousClicked: () -> Unit
    ) {
        onBuildMusicNotification = knotifClicked
        onPlayPauseClicked = playPauseClicked
        onNextClicked = nextClicked
        onPrevClicked = previousClicked
    }

    fun setOnBuildProgressKnotifListener(knotifClicked: (KNotifProgressData) -> Unit) {
        onBuildProgressNotification = knotifClicked
    }


    private fun buildMessageNotification(data: KNotifMessageData): NotificationCompat.Builder =
        buildBaseBuilder(data.appIcon)
            .setCustomContentView(messageNotificationSmallView(context, data))
            .setCustomBigContentView(messageNotificationLargeView(context, data))

    private fun buildMusicNotification(data: KNotifMusicData): NotificationCompat.Builder =
        buildBaseBuilder(data.appIcon)
            .setCustomContentView(musicNotificationSmallView(context, data))
            .setCustomBigContentView(musicNotificationLargeView(context, data))


    private fun buildProgressNotification(data: KNotifProgressData): NotificationCompat.Builder =
        buildBaseBuilder(data.appIcon)
            .setCustomContentView(progressNotificationSmallView(context, data))
            .setCustomBigContentView(progressNotificationLargeView(context, data))
}

private fun musicNotificationSmallView(context: Context, data: KNotifMusicData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_music_small).apply {
        setTextViewText(R.id.knotif_music_title, "${data.title} by ${data.artist}")

        setOnClickPendingIntent(
            /* viewId = */ R.id.music_small_layout,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_NOTIFICATION_CLICK,
                actionRequestCode = 2,
                notifId = data.id
            )
        )

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
            setImageViewBitmap(R.id.knotif_next, it.asAndroidBitmap())
        }
        data.icons.playIcon.let {
            val playPauseIcon =
                if (data.isPlaying) data.icons.pauseIcon else data.icons.playIcon
            setImageViewBitmap(R.id.knotif_play_pause, playPauseIcon?.asAndroidBitmap())
        }

        // Set pending intents
        setOnClickPendingIntent(
            /* viewId = */ R.id.music_large_layout,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_NOTIFICATION_CLICK,
                actionRequestCode = 2,
                notifId = data.id
            )
        )
        setOnClickPendingIntent(
            /* viewId = */ R.id.knotif_prev,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_PREVIOUS,
                actionRequestCode = 3,
                notifId = data.id
            )
        )
        setOnClickPendingIntent(
            /* viewId = */ R.id.knotif_next,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_NEXT,
                actionRequestCode = 4,
                notifId = data.id
            )
        )
        setOnClickPendingIntent(
            /* viewId = */ R.id.knotif_play_pause,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_PLAY_PAUSE,
                actionRequestCode = 5,
                notifId = data.id
            )
        )
    }
}


private fun messageNotificationSmallView(context: Context, data: KNotifMessageData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_message_small).apply {
        setTextViewText(R.id.knotif_title, data.title)
        setOnClickPendingIntent(
            /* viewId = */ R.id.message_small_layout,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_NOTIFICATION_CLICK,
                actionRequestCode = 1,
                notifId = data.id
            )
        )
    }
}


private fun messageNotificationLargeView(context: Context, data: KNotifMessageData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_message_large).apply {
        data.poster?.let { setImageViewBitmap(R.id.knotif_poster, it.asAndroidBitmap()) }
        setTextViewText(R.id.knotif_title, data.title)
        setTextViewText(R.id.knotif_message, data.message)
        setOnClickPendingIntent(
            /* viewId = */ R.id.message_large_layout,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_NOTIFICATION_CLICK,
                actionRequestCode = 1,
                notifId = data.id
            )
        )
    }
}

private fun progressNotificationSmallView(context: Context, data: KNotifProgressData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_progress_small).apply {
        setTextViewText(R.id.knotif_progress_title, data.title)
        setProgressBar(R.id.knotif_progress_bar, 100, data.progress, false)
        setTextViewText(R.id.knotif_progress_description, data.description)
        setOnClickPendingIntent(
            /* viewId = */ R.id.progress_small_layout,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_NOTIFICATION_CLICK,
                actionRequestCode = 0,
                notifId = data.id
            )
        )
    }
}

private fun progressNotificationLargeView(context: Context, data: KNotifProgressData): RemoteViews {
    return RemoteViews(context.packageName, R.layout.knotif_progress_large).apply {
        setTextViewText(R.id.knotif_progress_title, data.title)
        setTextViewText(R.id.knotif_progress_description, data.description)
        setProgressBar(R.id.knotif_progress_bar, 100, data.progress, data.indeterminate)
        setOnClickPendingIntent(
            /* viewId = */ R.id.progress_large_layout,
            /* pendingIntent = */ getPendingIntent(
                context = context,
                actionName = ACTION_NOTIFICATION_CLICK,
                actionRequestCode = 0,
                notifId = data.id
            )
        )
    }
}


private fun getPendingIntent(
    context: Context,
    actionName: String,
    actionRequestCode: Int,
    notifId: String
): PendingIntent {
    val intent = Intent(context, KnotifReceiver::class.java).apply {
        action = actionName
        putExtra(NOTIF_ID, notifId)
    }
    return PendingIntent.getBroadcast(
        context,
        actionRequestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun ImageBitmap.toIconCompat(): IconCompat {
    return IconCompat.createWithBitmap(this.asAndroidBitmap())
}