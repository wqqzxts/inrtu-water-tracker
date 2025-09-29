package com.example.watertracker.data.model

data class User(
    val id: Int = 1,
    val isMale: Boolean,
    val weight: Double,
    val height: Double,
    val age: Int,
    val dailyWaterNeed: Int
)