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
        WasmNotificationHelper.setOnBuildMusicKnotifListener(
            knotifClicked = { KNotifListeners.onBuildMusicNotification?.invoke(it) },
            playPauseClicked = { KNotifListeners.onPlayPauseClicked?.invoke() },
            nextClicked = { KNotifListeners.onNextClicked?.invoke() },
            previousClicked = { KNotifListeners.onPrevClicked?.invoke() })

        WasmNotificationHelper.setOnBuildMessageKnotifListener {
            KNotifListeners.onBuildMessageNotification?.invoke(it)
        }

        WasmNotificationHelper.setOnBuildProgressKnotifListener {
            KNotifListeners.onBuildProgressNotification?.invoke(it)
        }
    }

    actual override fun dismiss(notificationId: String) {
    }

    actual override fun dismissAll() {
    }

}