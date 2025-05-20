package org.kmp.shots.knotif
internal interface NotificationController{
    fun show(notification: KNotifData)
    fun dismiss(notificationId: String)
    fun dismissAll()
}

internal expect class NotificationService: NotificationController {
    override fun show(notification: KNotifData)
    override fun dismiss(notificationId: String)
    override fun dismissAll()
}