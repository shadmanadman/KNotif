package org.kmp.shots.knotif

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import knotif.composeapp.generated.resources.Knotif
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import knotif.composeapp.generated.resources.Res
import knotif.composeapp.generated.resources.compose_multiplatform
import knotif.composeapp.generated.resources.default_poster

@Composable
@Preview
fun App() {
    MaterialTheme {
        testMessageKnotif(
            imageResource(Res.drawable.default_poster),
            imageResource(Res.drawable.Knotif)
        )
    }
}

private fun testMessageKnotif(poster: ImageBitmap, appIcon: ImageBitmap) {
    val messageData = KNotifMessageData(
        id = "1",
        title = "This is a test",
        appName = "Kntif",
        message = "This is a test message",
        poster = poster,
        senderName = "",
        appIcon = appIcon,
        timestamp = 0L
    )
    Knotif.show(messageData)
}