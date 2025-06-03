package org.kmp.shots.knotif

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
    }
}

@Composable
private fun testMessageKnotif(poster: ImageBitmap, appIcon: ImageBitmap) {
    val messageData = KNotifMessageData(
        id = "1",
        title = "This is a test",
        appName = "Knotif",
        message = "This is a test message",
        poster = poster,
        appIcon = appIcon,
    )
    Knotif.show(messageData)
    Knotif.setOnBuildMessageKnotifListener {
        println("messahe clicjked")
    }
}

@Composable
private fun testMusicKnotif(
    poster: ImageBitmap,
    appIcon: ImageBitmap,
    playIcon: ImageBitmap,
    pauseIcon: ImageBitmap,
    nextIcon: ImageBitmap,
    previousIcon: ImageBitmap
) {
    val messageData = KNotifMusicData(
        id = "1",
        title = "This is a test",
        appName = "Knotif",
        icons = MusicIcons(
            poster = poster,
            playIcon = playIcon,
            pauseIcon = pauseIcon,
            nextIcon = nextIcon,
            previousIcon = previousIcon
        ),
        artist = "Artist test",
        isPlaying = true,
        appIcon = appIcon,
    )
    Knotif.show(messageData)
}

@Composable
private fun testProgressKnotif(appIcon: ImageBitmap) {
    val progressData = KNotifProgressData(
        id = "1",
        title = "This is a test",
        description = "This is a test description",
        appName = "Knotif",
        progress = 50,
        appIcon = appIcon
    )
    Knotif.show(progressData)
}