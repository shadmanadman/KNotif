package org.kmp.shots.knotif

import androidx.compose.runtime.Composable

object Knotif : NotificationController {
    private val notificationService: NotificationService = NotificationService()
    @Composable
    override fun show(notification: KNotifData) = notificationService.show(notification)

    override fun dismiss(notificationId: String) = notificationService.dismiss(notificationId)

    override fun dismissAll() = notificationService.dismissAll()
}