package org.kmp.shots.knotif

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.w3c.notifications.DENIED
import org.w3c.notifications.GRANTED
import org.w3c.notifications.Notification
import org.w3c.notifications.NotificationOptions
import org.w3c.notifications.NotificationPermission

internal object NotificationOverlayController {
    private val _activeOverlay = mutableStateOf<@Composable (() -> Unit)?>(null)
    val activeOverlay: State<(@Composable () -> Unit)?> = _activeOverlay

    fun show(content: @Composable () -> Unit) {
        _activeOverlay.value = content
    }

    fun hide() {
        _activeOverlay.value = null
    }
}

internal object WasmNotificationHelper {
    private var onBuildMessageNotification: ((KNotifMessageData) -> Unit)? = null
    private var onBuildMusicNotification: ((KNotifMusicData) -> Unit)? = null
    private var onBuildProgressNotification: ((KNotifProgressData) -> Unit)? = null

    private var onPlayPauseClicked: (() -> Unit)? = null
    private var onNextClicked: (() -> Unit)? = null
    private var onPrevClicked: (() -> Unit)? = null

    fun requestNotificationPermission(
        onPermissionGranted: () -> Unit
    ) {
        if (Notification.Companion.permission != NotificationPermission.GRANTED) {
            Notification.requestPermission { permission: NotificationPermission ->
                when (permission) {
                    NotificationPermission.GRANTED -> {
                        println("Notification permission granted.")
                        onPermissionGranted()
                    }

                    NotificationPermission.DENIED -> {
                        println("Notification permission denied.")
                    }
                }
            }
        } else {
            onPermissionGranted()
        }
    }


    fun showMessageNotification(data: KNotifMessageData) {
        requestNotificationPermission(onPermissionGranted = {
            val notificationOption = NotificationOptions(body = data.message)
            try {
                val notification = Notification(data.title, notificationOption)
                println("Notification created successfully for title: '${data.title}'")

                notification.onclick = {
                    onBuildMessageNotification?.invoke(data)
                }
                notification.onerror = { event ->
                    println("Notification error: ${event.type}")
                }

            } catch (e: Throwable) {
                println("Error creating notification: ${e.message}")
            }
        })
    }


    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    fun showMusicNotification(data: KNotifMusicData) {
        NotificationOverlayController.show {
            Row(
                modifier = Modifier.background(
                    data.style.backgroundColor.colorFromHex(), RoundedCornerShape(16.dp)
                ).padding(12.dp).clickable(onClick = { onBuildMusicNotification?.invoke(data) })
            ) {
                data.icons.poster?.let {
                    Image(bitmap = data.icons.poster, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = data.title,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        data.artist, style = MaterialTheme.typography.bodyMedium
                    )
                    Row {
                        data.icons.previousIcon?.let {
                            Icon(
                                modifier = Modifier.onClick(onClick = { onPrevClicked?.invoke() }),
                                bitmap = it,
                                contentDescription = null
                            )
                        }
                        data.isPlaying.getPlayIcon(data)?.let {
                            Icon(
                                modifier = Modifier.onClick(onClick = { onPlayPauseClicked?.invoke() }),
                                bitmap = it,
                                contentDescription = null
                            )
                        }
                        data.icons.nextIcon?.let {
                            Icon(
                                modifier = Modifier.onClick(onClick = { onNextClicked?.invoke() }),
                                bitmap = it,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalComposeUiApi::class)
    fun showProgressNotification(data: KNotifProgressData) {
        NotificationOverlayController.show {
            Column(
                Modifier.background(
                    data.style.backgroundColor.colorFromHex(), RoundedCornerShape(16.dp)
                ).padding(8.dp)
                    .clickable(onClick = { onBuildProgressNotification?.invoke(data) })
            ) {
                Text(data.title, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(progress = data.progress / 100f)
                Text("${data.progress}%")
            }
        }


    }

    fun setOnBuildMessageKnotifListener(
        knotifClicked: (KNotifMessageData) -> Unit
    ) {
        onBuildMessageNotification = knotifClicked
    }

    fun setOnBuildMusicKnotifListener(
        knotifClicked: (KNotifMusicData) -> Unit,
        playPauseClicked: () -> Unit,
        nextClicked: () -> Unit,
        previousClicked: () -> Unit
    ) {
        onBuildMusicNotification = knotifClicked
        onPlayPauseClicked = playPauseClicked
        onNextClicked = nextClicked
        onPrevClicked = previousClicked
    }

    fun setOnBuildProgressKnotifListener(knotifClicked: (KNotifProgressData) -> Unit) {
        onBuildProgressNotification = knotifClicked
    }
}


fun Boolean.getPlayIcon(data: KNotifMusicData): ImageBitmap? {
    return if (this) data.icons.playIcon
    else data.icons.pauseIcon
}
