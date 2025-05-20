package org.kmp.shots.knotif

import org.jetbrains.compose.resources.DrawableResource

/**
 * Base class for all types of notifications.
 * @property id Unique identifier for the notification.
 * @property title Title of the notification.
 * @property style Style of the notification.
 * @property appIcon Icon of the application.
 * @property position Applicable only on Desktop and Web. Ignored on mobile.
 */
sealed class KNotifData{
    abstract val id: String
    abstract val title: String
    abstract val style :KNotifStyle
    abstract val appIcon: String
    open val position: NotificationPosition = NotificationPosition.BottomCenter
}

enum class NotificationPosition {
    TopStart,
    TopCenter,
    TopEnd,
    BottomStart,
    BottomCenter,
    BottomEnd
}

data class KNotifMessageData(
    override val id: String,
    override val title: String,
    override val appIcon: String,
    val message: String,
    val senderName: String?,
    val timestamp: Long?,
    override val style: KNotifStyle = KNotifStyle.Default,
    override val position: NotificationPosition = NotificationPosition.BottomCenter
) : KNotifData()

data class KNotifMusicData(
    override val id: String,
    override val title: String,
    override val appIcon: String,
    val artist: String,
    val isPlaying: Boolean,
    val onPlayPause: (() -> Unit)? = null,
    val onNext: (() -> Unit)? = null,
    val onPrevious: (() -> Unit)? = null,
    val icons: MusicIcons = MusicIcons.Default,
    override val style: KNotifStyle = KNotifStyle.Default,
    override val position: NotificationPosition= NotificationPosition.BottomCenter
) : KNotifData()

data class MusicIcons(
    val playIcon: String,
    val pauseIcon: String,
    val nextIcon: String,
    val previousIcon: String
) {
    companion object {
        val Default = MusicIcons(
            playIcon = "ic_play",
            pauseIcon = "ic_pause",
            nextIcon = "ic_next",
            previousIcon = "ic_previous"
        )
    }
}

data class KNotifProgressData(
    override val id: String,
    override val title: String,
    override val appIcon: String,
    val progress: Int,
    val indeterminate: Boolean = false,
    val description: String? = null,
    override val style: KNotifStyle = KNotifStyle.Default,
    override val position: NotificationPosition= NotificationPosition.BottomCenter
) : KNotifData()

data class KNotifStyle(
    val backgroundColor: String = "#FFFFFF",
    val contentColor: String = "#000000",
    val cornerRadius: Float = 8f,
    val iconTint: String = "#000000"
) {
    companion object {
        val Default = KNotifStyle()
    }
}


