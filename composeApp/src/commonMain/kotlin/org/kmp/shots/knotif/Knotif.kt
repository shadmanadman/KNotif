package org.kmp.shots.knotif

object Knotif : NotificationController {
    private val notificationService: NotificationService = NotificationService()
    override fun show(notification: KNotifData) = notificationService.show(notification)

    override fun dismiss(notificationId: String) = notificationService.dismiss(notificationId)

    override fun dismissAll() = notificationService.dismissAll()
}