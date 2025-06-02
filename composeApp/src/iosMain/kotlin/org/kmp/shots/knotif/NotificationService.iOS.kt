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
    }

    actual override fun dismiss(notificationId: String) {
    }

    actual override fun dismissAll() {
    }

}