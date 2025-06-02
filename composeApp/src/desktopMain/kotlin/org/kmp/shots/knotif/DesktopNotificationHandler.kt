package org.kmp.shots.knotif

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.zIndex
import java.awt.Color
import java.awt.Component
import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType
import java.awt.Window
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities

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
        data.appIcon?.let {
            trayIcon.image = it.toAwtImage()
        }
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = data.appName

        trayIcon.addActionListener {
            onBuildMessageNotification?.invoke(data)
        }
        try {
            tray.add(trayIcon)
            trayIcon.displayMessage(
                data.title,
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
    @Composable
    fun showMusicNotification(data: KNotifMusicData) {
        Window(
            title = data.appName,
            undecorated = true,
            onCloseRequest = {},
            transparent = true,
            state = rememberWindowState(
                width = 360.dp, height = 180.dp, position = WindowPosition(
                    Alignment.BottomCenter
                )
            ),
            alwaysOnTop = true
        ) {
            MaterialTheme {
                Box(
                    Modifier.background(
                        data.style.backgroundColor.colorFromHex(),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    // Close Button
                    Text(
                        text = "X",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .zIndex(2f)
                            .padding(8.dp)
                            .clickable {
                                this@Window.window.dispose()
                            }
                    )
                    Row(
                        modifier = Modifier.padding(12.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onBuildMusicNotification?.invoke(data) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        data.icons.poster?.let {
                            Box(modifier = Modifier.size(90.dp).clip(RoundedCornerShape(16.dp))) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    bitmap = data.icons.poster,
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = data.title,
                                fontWeight = FontWeight.Bold
                            )
                            Text(data.artist, style = MaterialTheme.typography.body1)
                            Row(
                                modifier = Modifier.fillMaxWidth().zIndex(3f).padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                data.icons.previousIcon?.let {
                                    Icon(
                                        modifier = Modifier.size(24.dp)
                                            .onClick(onClick = { onPrevClicked?.invoke() }),
                                        bitmap = it,
                                        contentDescription = null
                                    )
                                }
                                data.isPlaying.getPlayIcon(data)?.let {
                                    Icon(
                                        modifier = Modifier.size(24.dp)
                                            .onClick(onClick = { onPlayPauseClicked?.invoke() }),
                                        bitmap = it,
                                        contentDescription = null
                                    )
                                }
                                data.icons.nextIcon?.let {
                                    Icon(
                                        modifier = Modifier.size(24.dp)
                                            .onClick(onClick = { onNextClicked?.invoke() }),
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

    @Composable
    fun showProgressNotification(data: KNotifProgressData) {
        Window(
            title = data.appName,
            undecorated = true,
            onCloseRequest = {},
            transparent = true,
            state = rememberWindowState(
                width = 300.dp,
                height = 150.dp,
                position = WindowPosition(alignment = Alignment.BottomCenter)
            ),
            alwaysOnTop = true
        ) {

            MaterialTheme {
                Box(
                    Modifier.background(
                        data.style.backgroundColor.colorFromHex(),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    // Close Button
                    Text(
                        text = "X",
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .zIndex(2f)
                            .padding(8.dp)
                            .clickable {
                                this@Window.window.dispose()
                            }
                    )
                    Column(
                        Modifier
                            .padding(8.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onBuildProgressNotification?.invoke(data) }
                    ) {
                        Row {
                            data.appIcon?.let {
                                Icon(
                                    modifier = Modifier.size(24.dp).padding(end = 8.dp),
                                    bitmap = it,
                                    contentDescription = null
                                )
                            }
                            Text(data.title, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(progress = data.progress / 100f)
                        Text("${data.progress}%")
                        Text("${data.description}", style = MaterialTheme.typography.subtitle2)
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

fun ImageBitmap.toAwtImage(): Image {
    val pixelMap = this.toPixelMap()
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val color = pixelMap[x, y]
            val awtColor = Color(
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt(),
                (color.alpha * 255).toInt()
            )
            bufferedImage.setRGB(x, y, awtColor.rgb)
        }
    }

    return bufferedImage
}
