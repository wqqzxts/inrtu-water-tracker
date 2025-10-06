package com.example.watertracker.data.repository

import com.example.watertracker.data.model.WaterConsumption
import com.example.watertracker.data.dao.WaterConsumptionDao

import java.util.Date

class WaterConsumptionRepository(private val waterConsumptionDao: WaterConsumptionDao) {
    fun logWaterConsumption(amount: Int) {
        val consumption = WaterConsumption(date = Date(), amount = amount)
        waterConsumptionDao.insertConsumption(consumption)
    }
    fun getTodayWaterConsumption(): Int = waterConsumptionDao.getTodayConsumption()
    fun getWeeklyWaterConsumption(): List<Pair<Date, Int>> = waterConsumptionDao.getWeeklyConsumption()
    fun getAverageWaterConsumption(): Int = waterConsumptionDao.getAverageDailyConsumption()
}