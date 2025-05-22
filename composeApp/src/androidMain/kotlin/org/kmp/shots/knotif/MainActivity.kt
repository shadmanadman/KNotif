package org.kmp.shots.knotif

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()

            val message = KNotifMessageData(
                id = "test_notification_1",
                title = "Test Title",
                message = "This is a test message",
                senderName = "",
                timestamp = null,
                appName = "Test App 34",
                appIcon = ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_launcher_foreground)
                    ?.toBitmap()?.asImageBitmap(),
                style = KNotifStyle(
                    backgroundColor = "#FF6200EE"
                )
            )
            AndroidNotificationHandler.show(message)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}