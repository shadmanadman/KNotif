package org.kmp.shots.knotif

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

const val NOTIF_ID = "notif_id"
class KnotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notifId = intent?.getStringExtra(NOTIF_ID) ?: return
        val notifData = AndroidNotificationHandler.getNotificationData(notifId) ?: return

        intent.action?.let { action ->
            when (action) {
                ACTION_PREVIOUS -> AndroidNotificationHandler.handlePrevClicked()
                ACTION_NEXT -> AndroidNotificationHandler.handleNextClicked()
                ACTION_PLAY_PAUSE -> AndroidNotificationHandler.handlePlayPauseClicked()
                ACTION_NOTIFICATION_CLICK -> {
                    when (notifData) {
                        is KNotifMessageData -> AndroidNotificationHandler.handleMessageClicked(
                            notifData
                        )

                        is KNotifMusicData -> AndroidNotificationHandler.handleMusicClicked(
                            notifData
                        )

                        is KNotifProgressData -> AndroidNotificationHandler.handleProgressClicked(
                            notifData
                        )
                    }
                }
            }
        }
    }
}