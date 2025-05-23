package org.kmp.shots.knotif

import platform.UserNotifications.*
import platform.Foundation.*

internal object IOSNotificationHandler {
    private var onBuildMessageNotification: ((KNotifMessageData) -> Unit)? = null
    private var onBuildMusicNotification: ((KNotifMusicData) -> Unit)? = null
    private var onBuildProgressNotification: ((KNotifProgressData) -> Unit)? = null

    private var onPlayPauseClicked: (() -> Unit)? = null
    private var onNextClicked: (() -> Unit)? = null
    private var onPrevClicked: (() -> Unit)? = null


    fun registerNotificationCategories() {
        val replyAction = UNTextInputNotificationAction.actionWithIdentifier(
            identifier = "REPLY_ACTION",
            title = "Reply",
            options = UNNotificationActionOptionNone
        )

        // Music control actions
        val playPauseAction = UNNotificationAction.actionWithIdentifier(
            identifier = "PLAY_PAUSE_ACTION",
            title = "Play/Pause",
            options = UNNotificationActionOptionNone
        )

        val nextAction = UNNotificationAction.actionWithIdentifier(
            identifier = "NEXT_ACTION",
            title = "Next",
            options = UNNotificationActionOptionNone
        )

        val previousAction = UNNotificationAction.actionWithIdentifier(
            identifier = "PREVIOUS_ACTION",
            title = "Previous",
            options = UNNotificationActionOptionNone
        )

        val messageCategory = UNNotificationCategory.categoryWithIdentifier(
            identifier = "MESSAGE_CATEGORY",
            actions = listOf(replyAction),
            intentIdentifiers = emptyList(),
            options = UNNotificationCategoryOptionNone
        )

        val musicCategory = UNNotificationCategory.categoryWithIdentifier(
            identifier = "MUSIC_CATEGORY",
            actions = listOf(previousAction, playPauseAction, nextAction),
            intentIdentifiers = listOf(),
            options = UNNotificationCategoryOptionNone
        )

        UNUserNotificationCenter.currentNotificationCenter().setNotificationCategories(
            setOf(messageCategory, musicCategory)
        )
    }


    fun showMessageNotification(data: KNotifMessageData) {
        val content = UNMutableNotificationContent().apply {
            setTitle(data.senderName ?: data.title)
            setSubtitle(data.appName)
            setBody(data.message)
            setCategoryIdentifier("MUSIC_CATEGORY")
        }

        data.poster?.let { imageBitmap ->
            val attachmentURL = saveImageBitmapToFileUrl(imageBitmap)
            attachmentURL?.let {
                val attachment = UNNotificationAttachment.attachmentWithIdentifier("poster", it, null, null)
                if (attachment != null) {
                    content.attachments = listOf(attachment)
                }
            }
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)

        val request = UNNotificationRequest.requestWithIdentifier(
            data.id,
            content,
            trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            error?.let {
                println("Failed to add message notification: ${it.localizedDescription}")
            }
        }
    }


    fun showMusicNotification(data: KNotifMusicData) {
        val content = UNMutableNotificationContent()
        content.setTitle(data.title)
        content.setSubtitle(data.appName)
        content.setBody(data.artist)
        content.setCategoryIdentifier("MUSIC_CATEGORY")

        data.icons.poster?.let { imageBitmap ->
            val attachmentURL = saveImageBitmapToFileUrl(imageBitmap)
            attachmentURL?.let {
                val attachment = UNNotificationAttachment.attachmentWithIdentifier("albumArt", it, null, null)
                if (attachment != null) {
                    content.attachments = listOf(attachment)
                }
            }
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)

        val request = UNNotificationRequest.requestWithIdentifier(
            data.id,
            content,
            trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            error?.let {
                println("Failed to add music notification: ${it.localizedDescription}")
            }
        }
    }

    // Register callbacks for action clicks
    fun setOnPlayPauseClicked(callback: () -> Unit) {
        onPlayPauseClicked = callback
    }

    fun setOnNextClicked(callback: () -> Unit) {
        onNextClicked = callback
    }

    fun setOnPrevClicked(callback: () -> Unit) {
        onPrevClicked = callback
    }


    // This function should be called from your iOS app's UNUserNotificationCenterDelegate
    fun handleAction(identifier: String) {
        when (identifier) {
            "PLAY_PAUSE_ACTION" -> onPlayPauseClicked?.invoke()
            "NEXT_ACTION" -> onNextClicked?.invoke()
            "PREVIOUS_ACTION" -> onPrevClicked?.invoke()
            // add other cases if needed
        }
    }
}