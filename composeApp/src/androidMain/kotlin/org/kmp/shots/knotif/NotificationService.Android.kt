package org.kmp.shots.knotif


internal actual class NotificationService : NotificationController {
    actual override fun show(notification: KNotifData) =
        AndroidNotificationHandler.show(notification)

    actual override fun dismiss(notificationId: String) =
        AndroidNotificationHandler.dismiss(notificationId)

    actual override fun dismissAll() = AndroidNotificationHandler.dismissAll()

}