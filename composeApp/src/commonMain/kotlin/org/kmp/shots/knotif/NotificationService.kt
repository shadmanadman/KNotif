package org.kmp.shots.knotif

import androidx.compose.runtime.Composable

internal interface NotificationController{
    @Composable
    fun show(notification: KNotifData)
    fun dismiss(notificationId: String)
    fun dismissAll()
}

internal expect class NotificationService(): NotificationController {
    @Composable
    override fun show(notification: KNotifData)
    override fun dismiss(notificationId: String)
    override fun dismissAll()
}