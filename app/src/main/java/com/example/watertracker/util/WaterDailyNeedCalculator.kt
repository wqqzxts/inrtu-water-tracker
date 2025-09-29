package com.example.watertracker.util

object WaterDailyNeedCalculator {
    fun calculateDailyWaterNeed(weight: Double, age: Int): Int {
        var baseNeed = weight * 35.0

        when {
            age < 30 -> baseNeed *= 1.0
            age < 55 -> baseNeed *= 0.95
            else -> baseNeed *= 0.9
        }

        return baseNeed.toInt().coerceIn(1500, 4000)
    }
}