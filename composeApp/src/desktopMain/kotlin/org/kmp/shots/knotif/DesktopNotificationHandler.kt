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
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType

object DesktopNotificationHandler {

    private var onBuildMessageNotification: ((KNotifMessageData) -> Unit)? = null
    private var onBuildMusicNotification: ((KNotifMusicData) -> Unit)? = null
    private var onBuildProgressNotification: ((KNotifProgressData) -> Unit)? = null

    private var onPlayPauseClicked: (() -> Unit)? = null
    private var onNextClicked: (() -> Unit)? = null
    private var onPrevClicked: (() -> Unit)? = null

    fun showSystemMessageNotification(data: KNotifMessageData) {
        if (SystemTray.isSupported().not()) return
        if (isMacOS()) {
            showMacOSNotification(data.title, data.message)
            return
        }

        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("icon.png")

        val trayIcon = TrayIcon(image, data.appName)
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = data.appName

        trayIcon.addActionListener {
            onBuildMessageNotification?.invoke(data)
        }
        try {
            tray.add(trayIcon)
            trayIcon.displayMessage(
                data.senderName ?: data.title,
                data.message,
                MessageType.INFO
            )
        } catch (e: Exception) {
            println("Unable to display system notification: ${e.message}")
        }
    }


    fun showMacOSNotification(title: String, message: String) {
        try {
            val process = ProcessBuilder(
                "osascript",
                "-e",
                "display notification \"$message\" with title \"$title\""
            ).start()
            process.waitFor()
        } catch (e: Exception) {
            println("Unable to display system notification: ${e.message}")
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    fun showMusicNotification(data: KNotifMusicData) {
        application {
            Window(
                title = data.appName,
                undecorated = true,
                onCloseRequest = {},
                transparent = true,
                state = rememberWindowState(width = 350.dp, height = 180.dp),
                alwaysOnTop = true
            ) {
                MaterialTheme {
                    Row(
                        modifier = Modifier.background(
                            data.style.backgroundColor.colorFromHex(),
                            RoundedCornerShape(16.dp)
                        ).padding(12.dp)
                            .clickable(onClick = { onBuildMusicNotification?.invoke(data) })
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
                            Text(data.artist, style = MaterialTheme.typography.body1)
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
        }
    }

    fun showProgressNotification(data: KNotifProgressData) {
        application {
            Window(
                title = data.appName,
                undecorated = true,
                onCloseRequest = {},
                transparent = true,
                state = rememberWindowState(width = 300.dp, height = 100.dp),
                alwaysOnTop = true
            ) {
                MaterialTheme {
                    Column(
                        Modifier
                            .background(
                                data.style.backgroundColor.colorFromHex(),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp)
                            .clickable(onClick = { onBuildProgressNotification?.invoke(data) })
                    ) {
                        Text(data.title, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(progress = data.progress / 100f)
                        Text("${data.progress}%")
                    }
                }
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

private fun isMacOS() = System.getProperty("os.name").lowercase().contains("mac")

fun Boolean.getPlayIcon(data: KNotifMusicData): ImageBitmap? {
    return if (this)
        data.icons.playIcon
    else
        data.icons.pauseIcon
}

