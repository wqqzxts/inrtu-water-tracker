package com.example.watertracker.util.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class NotificationScheduler(private val context: Context) {
    companion object {
        const val REQUEST_CODE = 123
        const val DAILY_SCHEDULER_REQUEST_CODE = 124
        const val INTERVAL_90_MINUTES = 90 * 60 * 1000L
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleWaterReminders() {
        cancelReminders()

        scheduleTodaysReminders()
    }

    fun scheduleDailyScheduler() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 55)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, DailySchedulerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_SCHEDULER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun scheduleTodaysReminders() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (timeInMillis < System.currentTimeMillis()) {
                val currentHour = get(Calendar.HOUR_OF_DAY)
                if (currentHour < 22) {
                    add(Calendar.MINUTE, 90)
                } else {
                    return
                }
            }
        }

        var reminderTime = calendar.timeInMillis
        val endTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        while (reminderTime <= endTime) {
            scheduleSingleReminder(reminderTime)
            reminderTime += INTERVAL_90_MINUTES
        }
    }

    private fun scheduleSingleReminder(triggerTime: Long) {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE + (triggerTime / INTERVAL_90_MINUTES).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancelReminders() {
        val dailyIntent = Intent(context, DailySchedulerReceiver::class.java)
        val dailyPendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_SCHEDULER_REQUEST_CODE,
            dailyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(dailyPendingIntent)

        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}