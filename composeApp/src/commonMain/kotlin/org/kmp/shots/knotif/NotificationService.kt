package org.kmp.shots.knotif

import androidx.compose.runtime.Composable

interface KNotifEventListeners {
    var onBuildMessageNotification: ((KNotifMessageData) -> Unit)?
    var onBuildMusicNotification: ((KNotifMusicData) -> Unit)?
    var onPlayPauseClicked: (() -> Unit)?
    var onNextClicked: (() -> Unit)?
    var onPrevClicked: (() -> Unit)?
    var onBuildProgressNotification: ((KNotifProgressData) -> Unit)?
}
object KNotifListeners : KNotifEventListeners {
    override var onBuildMessageNotification: ((KNotifMessageData) -> Unit)? = null
    override var onBuildMusicNotification: ((KNotifMusicData) -> Unit)? = null
    override var onPlayPauseClicked: (() -> Unit)? = null
    override var onNextClicked: (() -> Unit)? = null
    override var onPrevClicked: (() -> Unit)? = null
    override var onBuildProgressNotification: ((KNotifProgressData) -> Unit)? = null
}

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