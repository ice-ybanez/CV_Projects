package com.example.creativecommissionstracker.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import androidx.core.content.getSystemService
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationHelper {

    private const val PREFS_NAME = "notifications_prefs"
    private const val KEY_ENABLED = "notifications_enabled"

    fun isNotificationsEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(KEY_ENABLED, false)
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager: NotificationManager? = context.getSystemService()
            val channel = NotificationChannel(
                DueDateNotificationWorker.CHANNEL_ID,
                "Order due notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders when commission due dates are close."
            }
            manager?.createNotificationChannel(channel)
        }
    }

    // one-time notification
    // thresholdHours = how many hours before the due date to notify
    fun scheduleDueDateNotification(
        context: Context,
        orderTitle: String,
        dueDateMillis: Long,
        thresholdHours: Long = 24L
    ) {
        if (!isNotificationsEnabled(context)) return
        if (dueDateMillis <= 0L) return

        val now = System.currentTimeMillis()
        val triggerTime = dueDateMillis - thresholdHours * 60L * 60L * 1000L
        var delay = triggerTime - now
        if (delay < 0L) {
            // if due date is sooner than threshold, notify as soon as possible
            delay = 0L
        }

        // Ensure channel exists
        createNotificationChannel(context)

        val input = Data.Builder()
            .putString(DueDateNotificationWorker.KEY_TITLE, orderTitle)
            .putLong(DueDateNotificationWorker.KEY_DUE_MILLIS, dueDateMillis)
            .build()

        val request = OneTimeWorkRequestBuilder<DueDateNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(input)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}






