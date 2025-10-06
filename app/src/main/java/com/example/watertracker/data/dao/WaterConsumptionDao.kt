package com.example.watertracker.data.dao

import android.content.ContentValues
import java.util.Calendar
import java.util.Date

import com.example.watertracker.data.model.WaterConsumption

class WaterConsumptionDao(private val dbHelper: DatabaseHelper) {
    fun insertConsumption(consumption: WaterConsumption): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_DATE, consumption.date.time)
            put(DatabaseHelper.COLUMN_AMOUNT, consumption.amount)
            put(DatabaseHelper.COLUMN_TIMESTAMP, consumption.timestamp)
        }

        val result = db.insert(DatabaseHelper.TABLE_WATER_CONSUMPTION, null, values)
        return result != -1L
    }

    fun getTodayConsumption(): Int {
        val db = dbHelper.readableDatabase
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = calendar.timeInMillis

        val query = """
            SELECT SUM (${DatabaseHelper.COLUMN_AMOUNT}) as total
            FROM ${DatabaseHelper.TABLE_WATER_CONSUMPTION}
            WHERE ${DatabaseHelper.COLUMN_TIMESTAMP} >= ? AND ${DatabaseHelper.COLUMN_TIMESTAMP} < ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(startOfDay.toString(), endOfDay.toString()))

        return if (cursor.moveToFirst()) {
            val total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
            cursor.close()
            total
        } else {
            cursor.close()
            0
        }
    }

    fun getWeeklyConsumption(): List<Pair<Date, Int>> {
        val db = dbHelper.readableDatabase
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val consumptionList = mutableListOf<Pair<Date, Int>>()

        for (i in 0..6) {
            val dayStart = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = calendar.timeInMillis

            val query = """
                SELECT SUM(${DatabaseHelper.COLUMN_AMOUNT}) AS total
                FROM ${DatabaseHelper.TABLE_WATER_CONSUMPTION}
                WHERE ${DatabaseHelper.COLUMN_TIMESTAMP} >= ? AND ${DatabaseHelper.COLUMN_TIMESTAMP} < ?
            """.trimIndent()

            val cursor = db.rawQuery(query, arrayOf(dayStart.toString(), dayEnd.toString()))

            val total = if(cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("total"))
            } else {
                0
            }
            cursor.close()

            consumptionList.add(Pair(Date(dayStart), total))
        }
        return consumptionList
    }

    fun getAverageDailyConsumption(): Int {
        val weeklyData = getWeeklyConsumption()
        return if (weeklyData.isNotEmpty()) {
            weeklyData.sumOf { it.second } / weeklyData.size
        } else {
            0
        }
    }

    fun getConsumptionSince(sinceTime: Long): Int {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT SUM(${DatabaseHelper.COLUMN_AMOUNT}) AS total
            FROM ${DatabaseHelper.TABLE_WATER_CONSUMPTION}
            WHERE ${DatabaseHelper.COLUMN_TIMESTAMP} >= ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(sinceTime.toString()))

        return if (cursor.moveToFirst()) {
            val total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
            cursor.close()
            total
        } else {
            cursor.close()
            0
        }
    }
}