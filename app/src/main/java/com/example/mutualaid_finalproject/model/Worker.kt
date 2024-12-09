package com.example.mutualaid_finalproject.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data
import com.example.mutualaid_finalproject.MainActivity

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val message = inputData.getString("message") ?: "OMG SOMETHING HAPPENED!"

        // Show notification
        sendNotification(message)

        return Result.success()
    }

    private fun sendNotification(message: String) {
        val channelId = "post_acceptance_channel"
        val channelName = "Post Acceptance Notifications"

        // want to retriever the notification service
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android 8.0 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create a PendingIntent for when the notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java) // Replace with your target activity
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // build and show notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Post Status Update")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)

//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Make it a high priority for heads-up notification
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE) // Sound and vibration
            .setAutoCancel(true) // Dismiss the notification when clicked
            .setFullScreenIntent(pendingIntent, true) // Make the notification open MainActivity when tapped
            .build()

        notificationManager.notify(1, notification)
    }
}

