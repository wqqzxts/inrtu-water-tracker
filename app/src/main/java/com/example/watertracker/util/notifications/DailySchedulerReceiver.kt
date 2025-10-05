package com.example.watertracker.util.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.watertracker.data.dao.PreferencesManager

class DailySchedulerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val preferencesManager = PreferencesManager(context)

        if (preferencesManager.areNotificationsEnabled()) {
            val notificationScheduler = NotificationScheduler(context)
            notificationScheduler.scheduleDailyScheduler()
        }
    }
}
