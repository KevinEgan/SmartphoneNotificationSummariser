package com.example.notificationsummary

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/*
 This class implements NotificationListenerService. This service listens for notifications that
 that get posted to a user's phone. 
 Currently it logs text fields to discover the format of notifications.

 It is important that the user of this App gives permission to it (via settings) to access notifications.
 
 The service is declared in  AndroidManifest.xml. It uses android.permission.BIND_NOTIFICATION_LISTENER_SERVICE for security
 and restricts the service to this app only. 
 
 */
class NotificationLoggerService : NotificationListenerService() {

    // Logs when the service is first created by the system
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    // Logs when the notification listener connects/disconnects to the system
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Notification listener connected")
    }

    override fun onListenerDisconnected() {
        Log.d(TAG, "Notification listener disconnected")
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // StatusBarNotification contains the notification data
        // The notification's "extras" bundle contains common fields (title/text/etc.).
        val extras = sbn.notification.extras

        // Pull out common text fields. NUll checks here incase a field should be missing.
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()

        // Notification summary
        Log.d(TAG, """
            App: ${sbn.packageName}
            Title: $title
            Text: $text
            BigText: $bigText
            SubText: $subText
        """.trimIndent())
    }

    // This is a Logcat tag used to make filtering these messages easier & obvious looking
    private companion object {
        private const val TAG = "*****NOTIF"
    }
}
