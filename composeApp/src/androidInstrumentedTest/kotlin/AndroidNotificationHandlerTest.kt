import android.Manifest
import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.kmp.shots.knotif.AndroidNotificationHandler
import org.kmp.shots.knotif.AppContext
import org.kmp.shots.knotif.KNotifMessageData
import org.kmp.shots.knotif.KNotifMusicData
import org.kmp.shots.knotif.KNotifProgressData
import org.kmp.shots.knotif.KNotifStyle
import org.kmp.shots.knotif.R
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class AndroidNotificationHandlerTest : TestCase() {
//    @get:Rule
//    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.POST_NOTIFICATIONS
    )

    private val context = ApplicationProvider.getApplicationContext<Context>()


    @Before
    fun setup() {
        AppContext.setUp(context)
    }

    @Test
    fun testShowKnotifMessage() {
        val message = KNotifMessageData(
            id = "test_notification_1",
            title = "Test Title",
            message = "This is a test message",
            appName = "Test App 34",
            appIcon = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
                ?.toBitmap()?.asImageBitmap()!!,
            poster = null,
            style = KNotifStyle(
                backgroundColor = "#FF6200EE"
            )
        )
        AndroidNotificationHandler.show(message)

        // Let it show for a short period
        Thread.sleep(2000)

        // Check if it's posted
        val manager =
            ContextCompat.getSystemService(AppContext.get(), NotificationManager::class.java)!!
        val activeNotifications = manager.activeNotifications
        val found = activeNotifications.any { it.id == message.id.hashCode() }

        assertTrue("Notification should be shown", found)

        AndroidNotificationHandler.dismiss(message.id)
    }


    @Test
    fun testShowKnotifMusic() {
        val message = KNotifMusicData(
            id = "test_notification_1",
            title = "Test Title",
            appName = "Test App 34",
            artist = "test",
            isPlaying = false,
            appIcon = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
                ?.toBitmap()?.asImageBitmap()!!,
            style = KNotifStyle(
                backgroundColor = "#FF6200EE"
            )
        )
        AndroidNotificationHandler.show(message)

        // Let it show for a short period
        Thread.sleep(2000)

        // Check if it's posted
        val manager =
            ContextCompat.getSystemService(AppContext.get(), NotificationManager::class.java)!!
        val activeNotifications = manager.activeNotifications
        val found = activeNotifications.any { it.id == message.id.hashCode() }

        assertTrue("Notification should be shown", found)

        AndroidNotificationHandler.dismiss(message.id)
    }

    @Test
    fun testShowKnotifProgress() {
        val message = KNotifProgressData(
            id = "test_notification_1",
            title = "Test Title",
            appName = "Test App 34",
            progress = 30,
            appIcon = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
                ?.toBitmap()?.asImageBitmap()!!,
            style = KNotifStyle(
                backgroundColor = "#FF6200EE"
            )
        )
        AndroidNotificationHandler.show(message)

        // Let it show for a short period
        Thread.sleep(2000)

        // Check if it's posted
        val manager =
            ContextCompat.getSystemService(AppContext.get(), NotificationManager::class.java)!!
        val activeNotifications = manager.activeNotifications
        val found = activeNotifications.any { it.id == message.id.hashCode() }

        assertTrue("Notification should be shown", found)

        AndroidNotificationHandler.dismiss(message.id)
    }
}