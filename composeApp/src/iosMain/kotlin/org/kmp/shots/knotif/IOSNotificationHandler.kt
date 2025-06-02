package org.kmp.shots.knotif

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.setValue
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationAction
import platform.UserNotifications.UNNotificationActionOptionNone
import platform.UserNotifications.UNNotificationAttachment
import platform.UserNotifications.UNNotificationCategory
import platform.UserNotifications.UNNotificationCategoryOptionNone
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

const val PLAY_PAUSE_IDENTIFIER = "PLAY_PAUSE_ACTION"
const val NEXT_ACTION = "NEXT_ACTION"
const val PREVIOUS_ACTION = "PREVIOUS_ACTION"
const val MESSAGE_CATEGORY = "MESSAGE_CATEGORY"
const val PROGRESS_CATEGORY = "PROGRESS_CATEGORY"
const val MUSIC_CATEGORY = "MUSIC_CATEGORY"

internal object IOSNotificationHandler {
    private var onBuildMessageNotification: ((KNotifMessageData) -> Unit)? = null
    private var onBuildMusicNotification: ((KNotifMusicData) -> Unit)? = null
    private var onBuildProgressNotification: ((KNotifProgressData) -> Unit)? = null

    private var onPlayPauseClicked: (() -> Unit)? = null
    private var onNextClicked: (() -> Unit)? = null
    private var onPrevClicked: (() -> Unit)? = null


    fun registerNotificationCategories() {
        val playPauseAction = UNNotificationAction.actionWithIdentifier(
            identifier = PLAY_PAUSE_IDENTIFIER,
            title = "Play/Pause",
            options = UNNotificationActionOptionNone
        )

        val nextAction = UNNotificationAction.actionWithIdentifier(
            identifier = NEXT_ACTION, title = "Next", options = UNNotificationActionOptionNone
        )

        val previousAction = UNNotificationAction.actionWithIdentifier(
            identifier = PREVIOUS_ACTION,
            title = "Previous",
            options = UNNotificationActionOptionNone
        )

        val messageCategory = UNNotificationCategory.categoryWithIdentifier(
            identifier = MESSAGE_CATEGORY,
            actions = listOf<String>(),
            intentIdentifiers = listOf<String>(),
            options = UNNotificationCategoryOptionNone
        )

        val progressCategory = UNNotificationCategory.categoryWithIdentifier(
            identifier = PROGRESS_CATEGORY,
            actions = listOf<String>(),
            intentIdentifiers = listOf<String>(),
            options = UNNotificationCategoryOptionNone
        )


        val musicCategory = UNNotificationCategory.categoryWithIdentifier(
            identifier = MUSIC_CATEGORY,
            actions = listOf(previousAction, playPauseAction, nextAction),
            intentIdentifiers = listOf<String>(),
            options = UNNotificationCategoryOptionNone
        )

        UNUserNotificationCenter.currentNotificationCenter().setNotificationCategories(
            setOf(messageCategory, musicCategory, progressCategory)
        )
    }


    @OptIn(ExperimentalForeignApi::class)
    fun showMessageNotification(data: KNotifMessageData) {
        val content = UNMutableNotificationContent().apply {
            setTitle(data.title)
            setSubtitle(data.appName)
            setBody(data.message)
            setCategoryIdentifier(MESSAGE_CATEGORY)
        }

        data.poster?.let { imageBitmap ->
            val attachmentURL = imageBitmap.saveImageBitmapToFileUrl()
            attachmentURL?.let {
                println("Attachment URL: $it")
                val attachment =
                    UNNotificationAttachment.attachmentWithIdentifier("poster", it, null, null)
                if (attachment != null) {
                    content.setAttachments(listOf(attachment))
                }
            }
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(2.0, false)

        val request = UNNotificationRequest.requestWithIdentifier(
            MESSAGE_CATEGORY, content, trigger
        )
        setupNotificationDelegate(data)
        requestNotificationAuthorization {
            UNUserNotificationCenter.currentNotificationCenter()
                .addNotificationRequest(request) { error ->
                    error?.let {
                        println("Failed to add message notification: ${it.localizedDescription}")
                    }
                }
        }

    }

    fun showProgressNotification(data: KNotifProgressData) {
        val content = UNMutableNotificationContent().apply {
            setTitle(data.title)
            setSubtitle(data.appName)
            setBody("-----------------${data.progress}%------------------")
            setCategoryIdentifier(PROGRESS_CATEGORY)
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)

        val request = UNNotificationRequest.requestWithIdentifier(
            PROGRESS_CATEGORY, content, trigger
        )
        setupNotificationDelegate(data)
        requestNotificationAuthorization {
            UNUserNotificationCenter.currentNotificationCenter()
                .addNotificationRequest(request) { error ->
                    error?.let {
                        println("Failed to add message notification: ${it.localizedDescription}")
                    }
                }
        }

    }


    @OptIn(ExperimentalForeignApi::class)
    fun showMusicNotification(data: KNotifMusicData) {
        val content = UNMutableNotificationContent().apply {
            setTitle(data.title)
            setSubtitle(data.appName)
            setBody(data.artist)
            setCategoryIdentifier(MUSIC_CATEGORY)
        }

        data.icons.poster?.let { imageBitmap ->
            val attachmentURL = imageBitmap.saveImageBitmapToFileUrl()
            attachmentURL?.let {
                val attachment =
                    UNNotificationAttachment.attachmentWithIdentifier("albumArt", it, null, null)
                if (attachment != null) {
                    content.setValue(listOf(attachment), forKey = "attachments")
                }
            }
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)

        val request = UNNotificationRequest.requestWithIdentifier(
            MUSIC_CATEGORY, content, trigger
        )
        setupNotificationDelegate(data)
        requestNotificationAuthorization {
            UNUserNotificationCenter.currentNotificationCenter()
                .addNotificationRequest(request) { error ->
                    error?.let {
                        println("Failed to add music notification: ${it.localizedDescription}")
                    }
                }
        }
    }

    fun setOnBuildMessageKnotifListener(
        knotifClicked: (KNotifMessageData) -> Unit
    ) {
        onBuildMessageNotification = knotifClicked
    }

    fun setOnBuildMusicKnotifListener(
        knotifClicked: (KNotifMusicData) -> Unit,
        playPauseClicked: () -> Unit,
        nextClicked: () -> Unit,
        previousClicked: () -> Unit
    ) {
        onBuildMusicNotification = knotifClicked
        onPlayPauseClicked = playPauseClicked
        onNextClicked = nextClicked
        onPrevClicked = previousClicked
    }

    fun setOnBuildProgressKnotifListener(knotifClicked: (KNotifProgressData) -> Unit) {
        onBuildProgressNotification = knotifClicked
    }


    private fun handleAction(identifier: String, data: KNotifData) {
        when (identifier) {
            PLAY_PAUSE_IDENTIFIER -> onPlayPauseClicked?.invoke()
            NEXT_ACTION -> onNextClicked?.invoke()
            PREVIOUS_ACTION -> onPrevClicked?.invoke()
            MESSAGE_CATEGORY -> {
                if (data is KNotifMessageData)
                    onBuildMessageNotification?.invoke(data)
            }

            PROGRESS_CATEGORY -> {
                if (data is KNotifProgressData)
                    onBuildProgressNotification?.invoke(data)
            }

            MUSIC_CATEGORY -> {
                if (data is KNotifMusicData)
                    onBuildMusicNotification?.invoke(data)
            }
        }
    }


    @OptIn(ExperimentalForeignApi::class)
    private fun setupNotificationDelegate(notification: KNotifData) {
        class NotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
            override fun userNotificationCenter(
                center: UNUserNotificationCenter,
                willPresentNotification: UNNotification,
                withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
            ) {
                withCompletionHandler(
                    UNNotificationPresentationOptionAlert or UNNotificationPresentationOptionSound
                )
            }

            override fun userNotificationCenter(
                center: UNUserNotificationCenter,
                didReceiveNotificationResponse: UNNotificationResponse,
                withCompletionHandler: () -> Unit
            ) {
                handleAction(
                    didReceiveNotificationResponse.notification.request.identifier,
                    notification
                )
                println("Notification tapped: ${didReceiveNotificationResponse.notification.request.identifier}")
                withCompletionHandler()
            }
        }

        val delegate = NotificationDelegate()
        UNUserNotificationCenter.currentNotificationCenter().delegate = delegate
    }

}


@OptIn(ExperimentalForeignApi::class)
fun requestNotificationAuthorization(onGranted: () -> Unit) {
    val center = UNUserNotificationCenter.currentNotificationCenter()

    center.requestAuthorizationWithOptions(
        options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
        completionHandler = { granted, error ->
            println("Notification permission granted: $granted")
            onGranted()
            if (error != null) {
                println("Authorization error: ${error.localizedDescription}")
            }
        })
}