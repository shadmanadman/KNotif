package org.kmp.shots.knotif

import androidx.compose.runtime.Composable


internal actual class NotificationService : NotificationController {
    @Composable
    actual override fun show(notification: KNotifData) =
        AndroidNotificationHandler.show(notification)

    actual override fun dismiss(notificationId: String) =
        AndroidNotificationHandler.dismiss(notificationId)

    actual override fun dismissAll() = AndroidNotificationHandler.dismissAll()

}