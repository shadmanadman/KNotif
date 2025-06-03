package org.kmp.shots.knotif

import androidx.compose.runtime.Composable


internal actual class NotificationService : NotificationController {
    @Composable
    actual override fun show(notification: KNotifData) {
        when (notification) {
            is KNotifMessageData -> DesktopNotificationHandler.showSystemMessageNotification(
                notification
            )

            is KNotifMusicData -> DesktopNotificationHandler.showMusicNotification(notification)
            is KNotifProgressData -> DesktopNotificationHandler.showProgressNotification(
                notification
            )
        }
        DesktopNotificationHandler.setOnBuildMusicKnotifListener(
            knotifClicked = { KNotifListeners.onBuildMusicNotification?.invoke(it) },
            playPauseClicked = { KNotifListeners.onPlayPauseClicked?.invoke() },
            nextClicked = { KNotifListeners.onNextClicked?.invoke() },
            previousClicked = { KNotifListeners.onPrevClicked?.invoke() })

        DesktopNotificationHandler.setOnBuildMessageKnotifListener {
            KNotifListeners.onBuildMessageNotification?.invoke(it)
        }

        DesktopNotificationHandler.setOnBuildProgressKnotifListener {
            KNotifListeners.onBuildProgressNotification?.invoke(it)
        }
    }

    actual override fun dismiss(notificationId: String) {
    }

    actual override fun dismissAll() {
    }

}