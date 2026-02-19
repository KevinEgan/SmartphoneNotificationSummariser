package com.example.notificationsummary

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.app.NotificationManager

/*
 This class implements NotificationListenerService. This service listens for notifications that
 that get posted to a user's phone. 
 Currently it logs text fields to discover the format of notifications.

 It is important that the user of this App gives permission to it (via settings) to access notifications.
 
 The service is declared in  AndroidManifest.xml. It uses android.permission.BIND_NOTIFICATION_LISTENER_SERVICE for security
 and restricts the service to this app only. 
 
 */
class NotificationLoggerService : NotificationListenerService() {

    // Small hook to make this class/methods testable in the unit test class.
    // If this is null (normal app behavior), we just Logcat it.
    internal var notificationDataConsumer: ((NotificationData) -> Unit)? = null

    val notificationCategoriesToExclude = listOf( Notification.CATEGORY_TRANSPORT, Notification.CATEGORY_NAVIGATION,
        Notification.CATEGORY_WORKOUT, Notification.CATEGORY_LOCATION_SHARING, Notification.CATEGORY_PROMO,
        Notification.CATEGORY_STATUS, Notification.CATEGORY_STOPWATCH, Notification.CATEGORY_PROGRESS)

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
        val fileWriter = NotificationFileWrite("${filesDir}/notifications.jsonl")

        // Ranking checks the level of importance of notification
        val rankingMap = currentRanking ?:return
        val ranking = Ranking()

        if (!rankingMap.getRanking(sbn.key, ranking)){
            return
        }
        if (ranking.importance == NotificationManager.IMPORTANCE_HIGH){
            Log.d(TAG, "High importance notification found in ${sbn.packageName}")
        }
        if (ranking.importance< NotificationManager.IMPORTANCE_DEFAULT){
            Log.d(TAG, "Low impotance notification found in ${sbn.packageName}. Ignored.")
            return
        }
        if (sbn.notification.category in notificationCategoriesToExclude){
            Log.d(TAG, "Banned category; ${sbn.notification.category} from ${sbn.packageName}. Ignored.")
            return
        }

        
        // The notification's "extras" bundle contains common fields (title/text/etc.).
        val extras = sbn.notification.extras

        // Pull out common text fields. NUll checks here incase a field should be missing.
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()

        // Notification summary log
        /*
        Log.d(TAG, """
            App: ${sbn.packageName}
            Title: $title
            Text: $text
            BigText: $bigText
            SubText: $subText
        """.trimIndent()) */

        //isGroupSummary to filter out such messages like "139 messages from 2 chats" ie redundant info
        val isGroupSummary = sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0

        // Create NotificaionData object
        val data = NotificationData(System.currentTimeMillis(),sbn.postTime,
            sbn.packageName, sbn.groupKey,  isGroupSummary, title, text, bigText, subText, sbn.notification.category
        )

        val consumer = notificationDataConsumer
        if (consumer != null) {
            consumer(data)
        } else {
            // Log NotificationData for debugging
            Log.d(TAG, """
                NotificationData: $data
            """.trimIndent())
            // Write NotificationData to JSONL file
            try {
                Log.d(TAG, "Attempting to write to file: ${filesDir}/notifications.jsonl")
                fileWriter.writeToFile(data)
                Log.d(TAG, "Successfully wrote notification to file")
            } catch (error: Exception) {
                Log.d(TAG, "Error writing notification to file", error)
            }
        }
    }


    // This is a Logcat tag used to make filtering these messages easier & obvious looking
    private companion object {
        private const val TAG = "*****NOTIF"
    }
}
