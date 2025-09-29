package com.example.watertracker.data.repository

import com.example.watertracker.data.model.WaterConsumption
import com.example.watertracker.data.dao.WaterConsumptionDao

import java.util.Date

class WaterConsumptionRepository(private val waterConsumptionDao: WaterConsumptionDao) {
    suspend fun logWaterConsumption(amount: Int) {
        val consumption = WaterConsumption(date = Date(), amount = amount)
        waterConsumptionDao.insertConsumption(consumption)
    }
    suspend fun getTodayWaterConsumption(): Int = waterConsumptionDao.getTodayConsumption()
    suspend fun getWeeklyWaterConsumption(): List<Pair<Date, Int>> = waterConsumptionDao.getWeeklyConsumption()
    suspend fun getAverageWaterConsumption(): Int = waterConsumptionDao.getAverageDailyConsumption()
}