package org.kmp.shots.knotif

import androidx.compose.runtime.Composable


internal actual class NotificationService : NotificationController {

    @Composable
    actual override fun show(notification: KNotifData) {
        IOSNotificationHandler.registerNotificationCategories()
        when (notification) {
            is KNotifMessageData -> IOSNotificationHandler.showMessageNotification(notification)
            is KNotifMusicData -> IOSNotificationHandler.showMusicNotification(notification)
            is KNotifProgressData -> IOSNotificationHandler.showProgressNotification(notification)
        }

        IOSNotificationHandler.setOnBuildMusicKnotifListener(
            knotifClicked = { KNotifListeners.onBuildMusicNotification?.invoke(it) },
            playPauseClicked = { KNotifListeners.onPlayPauseClicked?.invoke() },
            nextClicked = { KNotifListeners.onNextClicked?.invoke() },
            previousClicked = { KNotifListeners.onPrevClicked?.invoke() })

        IOSNotificationHandler.setOnBuildMessageKnotifListener {
            KNotifListeners.onBuildMessageNotification?.invoke(it)
        }

        IOSNotificationHandler.setOnBuildProgressKnotifListener {
            KNotifListeners.onBuildProgressNotification?.invoke(it)
        }
    }

    actual override fun dismiss(notificationId: String) {
    }

    actual override fun dismissAll() {
    }

}