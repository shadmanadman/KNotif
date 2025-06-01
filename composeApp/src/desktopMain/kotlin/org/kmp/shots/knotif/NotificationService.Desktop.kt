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
    }

    actual override fun dismiss(notificationId: String) {
    }

    actual override fun dismissAll() {
    }

}