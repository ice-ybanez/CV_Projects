package com.example.creativecommissionstracker.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.creativecommissionstracker.R

// WorkManager notification

class DueDateNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: return Result.success()
        val dueDateMillis = inputData.getLong(KEY_DUE_MILLIS, 0L)


        // Make sure channel exists
        NotificationHelper.createNotificationChannel(applicationContext)

        val text = if (dueDateMillis > 0L) {
            "“$title” is due soon."
        } else {
            "“$title” is due soon."
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // found in res/drawable folder
            .setContentTitle("Order due soon")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            notify(id, notification)
        }

        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "due_date_channel"
        const val KEY_TITLE = "orderTitle"
        const val KEY_DUE_MILLIS = "dueDateMillis"
    }
}