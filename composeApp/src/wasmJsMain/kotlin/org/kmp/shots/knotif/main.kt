package org.kmp.shots.knotif

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import androidx.compose.ui.zIndex
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App()

        // Showing an overlay for music and progress notifications
        NotificationOverlayController.activeOverlay.value?.let { overlay ->
            Box(
                modifier = Modifier.fillMaxSize().zIndex(2f),
                contentAlignment = Alignment.BottomCenter
            ) {
                overlay()
            }
        }

    }
}