package org.kmp.shots.knotif

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
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
    @Composable
    fun showMusicNotification(data: KNotifMusicData) {
        var isClose by remember { (mutableStateOf(false)) }
        val boxAlpha by animateFloatAsState(
            targetValue = if (isClose) 0f else 1f,
            animationSpec = tween(durationMillis = 800)
        )
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                Modifier.size(340.dp, 120.dp)
                    .alpha(boxAlpha)
                    .zIndex(2f).border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    ).background(
                        data.style.backgroundColor.colorFromHex(),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                // Close Button
                Text(
                    text = "X",
                    fontSize = 18.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.TopEnd)
                        .zIndex(2f)
                        .padding(8.dp)
                        .clickable {
                            isClose = !isClose
                            println("isClose:$isClose")
                        }
                )
                Row(
                    modifier = Modifier.padding(12.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBuildMusicNotification?.invoke(data) }
                ) {
                    data.icons.poster?.let {
                        Box(modifier = Modifier.size(90.dp).clip(RoundedCornerShape(16.dp))) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                bitmap = it,
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        }
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
                        Row(
                            modifier = Modifier.fillMaxWidth().zIndex(3f).padding(top = 12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            data.icons.previousIcon?.let {
                                Icon(
                                    modifier = Modifier.padding(top = 12.dp).size(24.dp)
                                        .onClick(onClick = { onPrevClicked?.invoke() }),
                                    bitmap = it,
                                    contentDescription = null
                                )
                            }
                            data.isPlaying.getPlayIcon(data)?.let {
                                Icon(
                                    modifier = Modifier.padding(top = 12.dp).size(24.dp)
                                        .onClick(onClick = { onPlayPauseClicked?.invoke() }),
                                    bitmap = it,
                                    contentDescription = null
                                )
                            }
                            data.icons.nextIcon?.let {
                                Icon(
                                    modifier = Modifier.padding(top = 12.dp).size(24.dp)
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


    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun showProgressNotification(data: KNotifProgressData) {
        var isClose by remember { (mutableStateOf(false)) }
        val boxAlpha by animateFloatAsState(
            targetValue = if (isClose) 0f else 1f,
            animationSpec = tween(durationMillis = 800)
        )
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {

            Box(
                Modifier.size(280.dp, 90.dp)
                    .alpha(boxAlpha)
                    .zIndex(2f).border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    ).background(
                        data.style.backgroundColor.colorFromHex(),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                // Close Button
                Text(
                    text = "X",
                    fontSize = 18.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.TopEnd)
                        .zIndex(2f)
                        .padding(8.dp)
                        .clickable {
                            isClose = !isClose
                            println("isClose:$isClose")
                        }
                )
                Column(
                    Modifier.padding(8.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBuildProgressNotification?.invoke(data) }
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        data.appIcon?.let {
                            Icon(
                                modifier = Modifier.size(24.dp).padding(end = 4.dp),
                                bitmap = it,
                                contentDescription = null
                            )
                        }
                        Text(data.title, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(progress = data.progress / 100f)
                    Text("${data.progress}%")
                    Text("${data.description}", style = MaterialTheme.typography.bodySmall)

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


fun Boolean.getPlayIcon(data: KNotifMusicData): ImageBitmap? {
    return if (this) data.icons.playIcon
    else data.icons.pauseIcon
}

fun ImageBitmap.toDataUrl(): String {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    canvas.width = width
    canvas.height = height

    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.drawImage(this.asHtmlImageElement(), 0.0, 0.0)

    return canvas.toDataURL("image/png") // returns data:image/png;base64,...
}

fun ImageBitmap.asHtmlImageElement(): HTMLImageElement {
    val img = document.createElement("img") as HTMLImageElement
    // Assuming you can assign a base64 or existing src
    // You might need to store the bitmap into a canvas and extract the data URL like above
    return img
}

fun ByteArray.toBase64DataUrl(): String {
    val base64 = window.btoa(this.joinToString("") { it.toInt().toChar().toString() })
    return "data:image/png;base64,$base64"
}

