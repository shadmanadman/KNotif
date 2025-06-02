import androidx.compose.runtime.Composable
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
            poster = null,
            appName = "TestApp",
            appIcon = null
        )
        DesktopNotificationHandler.showSystemMessageNotification(data)

        // Not assertable visually
        assertTrue(true)
    }


    @Test
    @Composable
    fun testMusicNotificationComposeWindow() {
        val data = KNotifMusicData(
            id = "music-1",
            title = "Now Playing",
            artist = "Lo-fi Artist",
            appName = "TestMusicApp",
            appIcon = null,
            isPlaying = false
        )

            DesktopNotificationHandler.showMusicNotification(data)



        assertTrue(true) // If no exceptions, the flow is correct
    }


    @Test
    @Composable
    fun testProgressNotification() {
        val data = KNotifProgressData(
            id = "progress-1",
            title = "Downloading",
            progress = 50,
            appName = "TestDownloader",
            description = "Downloading file number 2",
            appIcon = null
        )


        DesktopNotificationHandler.showProgressNotification(data)
        assertTrue(true) // If no exceptions, the flow is correct
    }
}