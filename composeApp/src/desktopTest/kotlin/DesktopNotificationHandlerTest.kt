import androidx.compose.ui.test.junit4.createComposeRule
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.kmp.shots.knotif.DesktopNotificationHandler
import org.kmp.shots.knotif.KNotifMessageData
import org.kmp.shots.knotif.KNotifMusicData
import org.kmp.shots.knotif.KNotifProgressData
import java.awt.SystemTray
import kotlin.test.Test


class DesktopNotificationHandlerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMessageNotification() {
        if (!SystemTray.isSupported()) {
            println("System tray not supported on this platform.")
            return
        }

        val data = KNotifMessageData(
            id = "msg-1",
            title = "Hello",
            message = "You've got a new message!",
            senderName = "ChatGPT",
            timestamp = System.currentTimeMillis(),
            poster = null,
            appName = "TestApp",
            appIcon = null
        )
        DesktopNotificationHandler.showSystemMessageNotification(data)

        // Not assertable visually
        assertTrue(true)
    }


    @Test
    fun testMusicNotificationComposeWindow() = runBlocking {
        val data = KNotifMusicData(
            id = "music-1",
            title = "Now Playing",
            artist = "Lo-fi Artist",
            appName = "TestMusicApp",
            appIcon = null,
            isPlaying = false
        )

        withContext(Dispatchers.Default) {
            DesktopNotificationHandler.showMusicNotification(data)
        }

        delay(2000)

        assertTrue(true) // If no exceptions, the flow is correct
    }


    @Test
    fun testProgressNotification() = runBlocking {
        val data = KNotifProgressData(
            id = "progress-1",
            title = "Downloading",
            progress = 50,
            appName = "TestDownloader",
            appIcon = null
        )

        withContext(Dispatchers.Default) {
            DesktopNotificationHandler.showProgressNotification(data)
        }

        delay(3000)

        assertTrue(true) // If no exceptions, the flow is correct
    }
}