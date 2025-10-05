package com.example.watertracker.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

import com.example.watertracker.data.dao.DatabaseHelper
import com.example.watertracker.data.dao.WaterConsumptionDao
import com.example.watertracker.util.NotificationHelper

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DatabaseHelper(context)
        val waterConsumptionDao = WaterConsumptionDao(dbHelper)

        val currentTime = System.currentTimeMillis()
        val oneAndHalfHoursAgo = currentTime - (90 * 60 * 1000)

        val recentConsumption = waterConsumptionDao.getConsumptionSince(oneAndHalfHoursAgo)

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        if (recentConsumption == 0 && currentHour in 10..22) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showWaterReminder()
        }
    }
}