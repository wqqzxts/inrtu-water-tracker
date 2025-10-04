package com.example.watertracker.data.dao

import android.content.ContentValues

import com.example.watertracker.data.model.User

class UserDao(private val dbHelper: DatabaseHelper) {
    fun insertUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_ID, user.id)
            put(DatabaseHelper.COLUMN_IS_MALE, user.isMale)
            put(DatabaseHelper.COLUMN_WEIGHT, user.weight)
            put(DatabaseHelper.COLUMN_HEIGHT, user.height)
            put(DatabaseHelper.COLUMN_AGE, user.age)
            put(DatabaseHelper.COLUMN_DAILY_WATER_NEED, user.dailyWaterNeed)
        }

        val result = db.insert(DatabaseHelper.TABLE_USER, null, values)
        return result != -1L
    }

    fun getUser(): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USER,
            null,
            null,
            null,
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                isMale = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_MALE))  == 1,
                weight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT)),
                height = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT)),
                age = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE)),
                dailyWaterNeed = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAILY_WATER_NEED))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun updateUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_IS_MALE, user.isMale)
            put(DatabaseHelper.COLUMN_WEIGHT, user.weight)
            put(DatabaseHelper.COLUMN_HEIGHT, user.height)
            put(DatabaseHelper.COLUMN_AGE, user.age)
            put(DatabaseHelper.COLUMN_DAILY_WATER_NEED, user.dailyWaterNeed)
        }

        val result = db.update(
            DatabaseHelper.TABLE_USER,
            values,
            "${DatabaseHelper.COLUMN_USER_ID} = ?",
            arrayOf(user.id.toString())
        )
        return result > 0
    }

    fun userExists(): Boolean {
        return getUser() != null
    }
}