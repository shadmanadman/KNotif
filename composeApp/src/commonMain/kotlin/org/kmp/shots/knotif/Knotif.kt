package org.kmp.shots.knotif

import androidx.compose.runtime.Composable

object Knotif : NotificationController {
    private val notificationService: NotificationService = NotificationService()
    @Composable
    override fun show(notification: KNotifData) = notificationService.show(notification)

    override fun dismiss(notificationId: String) = notificationService.dismiss(notificationId)

    override fun dismissAll() = notificationService.dismissAll()

    fun setOnBuildMessageKnotifListener(
        knotifClicked: (KNotifMessageData) -> Unit
    ) {
        KNotifListeners.onBuildMessageNotification = knotifClicked
    }

    fun setOnBuildMusicKnotifListener(
        knotifClicked: (KNotifMusicData) -> Unit,
        playPauseClicked: () -> Unit,
        nextClicked: () -> Unit,
        previousClicked: () -> Unit
    ) {
        KNotifListeners.onBuildMusicNotification = knotifClicked
        KNotifListeners.onPlayPauseClicked = playPauseClicked
        KNotifListeners.onNextClicked = nextClicked
        KNotifListeners.onPrevClicked = previousClicked
    }

    fun setOnBuildProgressKnotifListener(knotifClicked: (KNotifProgressData) -> Unit) {
        KNotifListeners.onBuildProgressNotification = knotifClicked
    }
}