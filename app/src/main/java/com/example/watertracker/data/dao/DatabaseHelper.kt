package com.example.watertracker.data.dao

import android.content.Context

import android.database.sqlite.SQLiteDatabase
import  android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "water_logger.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USER = "user"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_IS_MALE = "is_male"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_AGE = "age"
        const val COLUMN_DAILY_WATER_NEED = "daily_water_need"

        const val TABLE_WATER_CONSUMPTION = "water_consumption"
        const val COLUMN_CONSUMPTION_ID = "id"
        const val COLUMN_DATE = "date"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USER(
                $COLUMN_USER_ID INTEGER PRIMARY KEY,
                $COLUMN_IS_MALE BOOLEAN NOT NULL,
                $COLUMN_WEIGHT REAL NOT NULL,
                $COLUMN_HEIGHT REAL NOT NULL,
                $COLUMN_AGE INTEGER NOT NULL,
                $COLUMN_DAILY_WATER_NEED INTEGER NOT NULL
            )
        """.trimIndent()

        val createWaterConsumptionTable = """
            CREATE TABLE $TABLE_WATER_CONSUMPTION(
                $COLUMN_CONSUMPTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DATE INTEGER NOT NULL,
                $COLUMN_AMOUNT INTEGER NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createWaterConsumptionTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WATER_CONSUMPTION")
        onCreate(db)
    }
}