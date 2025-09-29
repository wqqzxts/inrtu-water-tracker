package com.example.watertracker.data.model

import java.util.Date

data class WaterConsumption(
    val id: Int = 0,
    val date: Date,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis()
)