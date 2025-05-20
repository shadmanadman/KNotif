import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import org.jetbrains.compose.resources.DrawableResource
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.kmp.shots.knotif.AndroidNotificationHandler
import org.kmp.shots.knotif.AppContext
import org.kmp.shots.knotif.KNotifMessageData
import org.kmp.shots.knotif.KNotifStyle
import org.kmp.shots.knotif.MainActivity
import kotlin.jvm.java
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class AndroidNotificationHandlerTest : TestCase() {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val context = ApplicationProvider.getApplicationContext<Context>()


    @Before
    fun setup() {
        AppContext.setUp(context)
    }

    @Test
    fun testShowKnotifMessage(){
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

        // Let it show for a short period
        Thread.sleep(2000)

        // Check if it's posted
        val manager = ContextCompat.getSystemService(AppContext.get(), NotificationManager::class.java)!!
        val activeNotifications = manager.activeNotifications
        val found = activeNotifications.any { it.id == message.id.hashCode() }

        assertTrue("Notification should be shown", found)

        AndroidNotificationHandler.dismiss(message.id)
    }
}