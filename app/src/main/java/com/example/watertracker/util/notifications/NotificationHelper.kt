package com.example.watertracker.util.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

import com.example.watertracker.R
import com.example.watertracker.ui.MainActivity

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "water_reminder_channel"
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Напоминания об употреблении воды",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Напоминания об употреблении воды каждые 1.5 часа с 10 до 22, если в последние 1.5 часа не было записи"
        }

        notificationManager.createNotificationChannel(channel)
    }

    fun showWaterReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("\uD83D\uDCA7 Время пить воду!")
            .setContentText("Не забудьте попить воды")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelReminders() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}