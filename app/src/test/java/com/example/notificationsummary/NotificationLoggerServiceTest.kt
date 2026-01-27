package com.example.notificationsummary

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Process
import android.service.notification.StatusBarNotification
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

// Mocks Android classes
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NotificationLoggerServiceTest {

    @Test
    fun testNotificationWithContent() {
        val ctx = RuntimeEnvironment.getApplication()
        createChannel(ctx)

        val notif = Notification.Builder(ctx, "test-ch")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Test Title")
            .setContentText("Test message")
            .build()

        val sbn = buildStatusBarNotif(ctx, notif, 1000L)
        
        val service = Robolectric.buildService(NotificationLoggerService::class.java).create().get()
        var result: NotificationData? = null
        service.notificationDataConsumer = { result = it }

        service.onNotificationPosted(sbn)

        assertNotNull(result)
        assertEquals("Test Title", result?.title)
        assertEquals("Test message", result?.text)
        assertEquals(1000L, result?.postTimeMs)
    }

    @Test
    fun testEmptyNotification() {
        val ctx = RuntimeEnvironment.getApplication()
        createChannel(ctx)

        val notif = Notification.Builder(ctx, "test-ch")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        val sbn = buildStatusBarNotif(ctx, notif, 500L)
        
        val service = Robolectric.buildService(NotificationLoggerService::class.java).create().get()
        var result: NotificationData? = null
        service.notificationDataConsumer = { result = it }

        service.onNotificationPosted(sbn)

        assertNotNull(result)
        assertNull(result?.title)
        assertNull(result?.text)
    }

    private fun createChannel(ctx: Context) {
        val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mgr.createNotificationChannel(NotificationChannel("test-ch", "Test", NotificationManager.IMPORTANCE_DEFAULT))
    }

    private fun buildStatusBarNotif(ctx: Context, notif: Notification, postTime: Long): StatusBarNotification {
        // try to find a working constructor
        val user = Process.myUserHandle()
        for (ctor in StatusBarNotification::class.java.constructors) {
            try {
                val params = ctor.parameterTypes
                if (params.any { it == Notification::class.java }) {
                    val args = params.map { type ->
                        when (type) {
                            String::class.java -> ctx.packageName
                            Int::class.javaPrimitiveType -> 1
                            Notification::class.java -> notif
                            android.os.UserHandle::class.java -> user
                            java.lang.Long.TYPE -> postTime
                            else -> null
                        }
                    }.toTypedArray()
                    return ctor.newInstance(*args) as StatusBarNotification
                }
            } catch (e: Exception) {
                continue
            }
        }
        throw RuntimeException("Couldn't create StatusBarNotification")
    }
}
