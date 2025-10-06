package com.example.watertracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.example.watertracker.data.repository.WaterConsumptionRepository

class WaterConsumptionViewModel(private val repository: WaterConsumptionRepository): ViewModel() {
    private val _todayConsumption = MutableStateFlow(0)
    val todayConsumption: StateFlow<Int> = _todayConsumption

    private val _weeklyConsumption = MutableStateFlow<List<Pair<Long, Int>>>(emptyList())
    val weeklyConsumption: StateFlow<List<Pair<Long, Int>>> = _weeklyConsumption

    private val _averageConsumption = MutableStateFlow(0)
    val averageConsumption: StateFlow<Int> = _averageConsumption

    init {
        loadTodayConsumption()
        loadWeeklyStats()
    }

    fun logWater(amount: Int) {
        viewModelScope.launch {
            repository.logWaterConsumption(amount)
            loadTodayConsumption()
            loadWeeklyStats()
        }
    }

    private fun loadTodayConsumption() {
        viewModelScope.launch {
            _todayConsumption.value = repository.getTodayWaterConsumption()
        }
    }

    private fun loadWeeklyStats() {
        viewModelScope.launch {
            val weeklyData = repository.getWeeklyWaterConsumption()
            _weeklyConsumption.value = weeklyData.map { it.first.time to it.second }
            _averageConsumption.value = repository.getAverageWaterConsumption()
        }
    }
}