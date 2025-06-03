package org.kmp.shots.knotif

import androidx.compose.runtime.Composable


internal actual class NotificationService : NotificationController {
    @Composable
    actual override fun show(notification: KNotifData) {
        AndroidNotificationHandler.show(notification)
        AndroidNotificationHandler.setOnBuildMessageKnotifListener {
            KNotifListeners.onBuildMessageNotification?.invoke(it)
        }
        AndroidNotificationHandler.setOnBuildProgressKnotifListener {
            KNotifListeners.onBuildProgressNotification?.invoke(it)
        }
        AndroidNotificationHandler.setOnBuildMusicKnotifListener(
            knotifClicked = { KNotifListeners.onBuildMusicNotification?.invoke(it) },
            playPauseClicked = { KNotifListeners.onPlayPauseClicked?.invoke() },
            nextClicked = { KNotifListeners.onNextClicked?.invoke() },
            previousClicked = { KNotifListeners.onPrevClicked?.invoke() })
    }

    actual override fun dismiss(notificationId: String) =
        AndroidNotificationHandler.dismiss(notificationId)

    actual override fun dismissAll() = AndroidNotificationHandler.dismissAll()

}