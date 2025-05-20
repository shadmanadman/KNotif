package org.kmp.shots.knotif

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val message = KNotifMessageData(
            id = "test_notification_1",
            title = "Test Title",
            message = "This is a test message",
            senderName = "",
            timestamp = null,
            style = KNotifStyle(
                backgroundColor = "#FF6200EE"
            )
        )
        AndroidNotificationHandler.show(message)
        setContent {
            App()

        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}