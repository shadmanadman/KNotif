package org.kmp.shots.knotif

import androidx.compose.ui.graphics.ImageBitmap

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
    abstract val appIcon: ImageBitmap
    abstract val appName: String
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
    override val appIcon: ImageBitmap,
    override val appName: String,
    val message: String,
    val poster: ImageBitmap? = null,
    override val style: KNotifStyle = KNotifStyle.Default,
    override val position: NotificationPosition = NotificationPosition.BottomCenter
) : KNotifData(){

}

data class KNotifMusicData(
    override val id: String,
    override val title: String,
    override val appIcon: ImageBitmap,
    override val appName: String,
    val artist: String,
    val isPlaying: Boolean,
    val icons: MusicIcons = MusicIcons.Default,
    override val style: KNotifStyle = KNotifStyle.Default,
    override val position: NotificationPosition= NotificationPosition.BottomCenter
) : KNotifData()

data class MusicIcons(
    val playIcon: ImageBitmap? = null,
    val pauseIcon: ImageBitmap?= null,
    val nextIcon: ImageBitmap?= null,
    val previousIcon: ImageBitmap?= null,
    val poster: ImageBitmap?= null
) {
    companion object {
        val Default = MusicIcons()
    }
}

data class KNotifProgressData(
    override val id: String,
    override val title: String,
    override val appIcon: ImageBitmap,
    override val appName: String,
    val progress: Int,
    val indeterminate: Boolean = false,
    val description: String? = null,
    override val style: KNotifStyle = KNotifStyle.Default,
    override val position: NotificationPosition= NotificationPosition.BottomCenter
) : KNotifData()

/**
 * Notification style
 * @property backgroundColor Applicable only on Desktop and Web for [KNotifProgressData] or [KNotifMusicData] type.
 */
data class KNotifStyle(
    val backgroundColor: String = "#FFFFFF",
) {
    companion object {
        val Default = KNotifStyle()
    }
}


