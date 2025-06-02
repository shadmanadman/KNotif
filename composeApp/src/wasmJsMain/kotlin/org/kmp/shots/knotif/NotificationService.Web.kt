package org.kmp.shots.knotif

import androidx.compose.runtime.Composable


internal actual class NotificationService : NotificationController {
    @Composable
    actual override fun show(notification: KNotifData) {
        when (notification) {
            is KNotifMessageData -> WasmNotificationHelper.showMessageNotification(notification)
            is KNotifMusicData -> WasmNotificationHelper.showMusicNotification(notification)
            is KNotifProgressData -> WasmNotificationHelper.showProgressNotification(notification)
        }
    }

    actual override fun dismiss(notificationId: String) {
    }

    actual override fun dismissAll() {
    }

}