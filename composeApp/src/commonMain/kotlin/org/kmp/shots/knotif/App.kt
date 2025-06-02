package org.kmp.shots.knotif

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import knotif.composeapp.generated.resources.Res
import knotif.composeapp.generated.resources.default_poster
import knotif.composeapp.generated.resources.ic_default_app_icon
import knotif.composeapp.generated.resources.ic_default_next
import knotif.composeapp.generated.resources.ic_default_pause
import knotif.composeapp.generated.resources.ic_default_play
import knotif.composeapp.generated.resources.ic_default_prev
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
//        testMessageKnotif(
//            imageResource(Res.drawable.default_poster),
//            imageResource(Res.drawable.ic_default_app_icon)
//        )

        //testProgressKnotit(imageResource(Res.drawable.ic_default_app_icon))

    }

    testMusicKnotif(
        imageResource(Res.drawable.default_poster),
        imageResource(Res.drawable.ic_default_app_icon),
        imageResource(Res.drawable.ic_default_play),
        imageResource(Res.drawable.ic_default_pause),
        imageResource(Res.drawable.ic_default_next),
        imageResource(Res.drawable.ic_default_prev)
    )
}

@Composable
private fun testMessageKnotif(poster: ImageBitmap, appIcon: ImageBitmap) {
    val messageData = KNotifMessageData(
        id = "1",
        title = "This is a test",
        appName = "Kntif",
        message = "This is a test message",
        poster = poster,
        appIcon = appIcon,
    )
    Knotif.show(messageData)
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
        appName = "Kntif",
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
private fun testProgressKnotit(appIcon: ImageBitmap) {
    val progressData = KNotifProgressData(
        id = "1",
        title = "This is a test",
        description = "This is a test description",
        appName = "Kntif",
        progress = 50,
        appIcon = appIcon
    )
    Knotif.show(progressData)
}