package com.example.watertracker.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.text.InputType

import com.example.watertracker.R
import com.example.watertracker.data.dao.DatabaseHelper
import com.example.watertracker.data.dao.UserDao
import com.example.watertracker.data.dao.WaterConsumptionDao
import com.example.watertracker.data.repository.UserRepository
import com.example.watertracker.data.repository.WaterConsumptionRepository
import com.example.watertracker.viewmodel.UserViewModel
import com.example.watertracker.viewmodel.UserViewModelFactory
import com.example.watertracker.viewmodel.WaterConsumptionViewModel
import com.example.watertracker.viewmodel.WaterConsumptionViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var waterConsumptionViewModel: WaterConsumptionViewModel
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var accountButton: Button
    private lateinit var todayProgressText: TextView
    private lateinit var waterProgressBar: ProgressBar
    private lateinit var log250Button: Button
    private lateinit var log500Button: Button
    private lateinit var logCustomButton: Button
    private lateinit var lineChart: LineChart
    private lateinit var averageConsumptionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this@MainActivity)

        initViews()
        setupViewModel()
        checkUserExists()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        accountButton = findViewById(R.id.accountButton)
        todayProgressText = findViewById(R.id.todayProgressText)
        waterProgressBar = findViewById(R.id.waterProgressBar)
        log250Button = findViewById(R.id.log250Button)
        log500Button = findViewById(R.id.log500Button)
        logCustomButton = findViewById(R.id.logCustomButton)
        lineChart = findViewById(R.id.lineChart)
        averageConsumptionText = findViewById(R.id.averageConsumptionText)
    }

    private fun setupViewModel() {
        val userDao = UserDao(dbHelper)
        val waterConsumptionDao = WaterConsumptionDao(dbHelper)

        val userRepository = UserRepository(userDao)
        val waterConsumptionRepository = WaterConsumptionRepository(waterConsumptionDao)

        userViewModel = ViewModelProvider(this, UserViewModelFactory(userRepository))[UserViewModel::class.java]
        waterConsumptionViewModel = ViewModelProvider(this, WaterConsumptionViewModelFactory(waterConsumptionRepository))[WaterConsumptionViewModel::class.java]
    }

    private fun checkUserExists() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.userExists.collect { userExists ->
                    if (!userExists) {
                        navigateToRegisterActivity()
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.user.collect { user ->
                    user?.let {
                        updateProgressBar(it.dailyWaterNeed)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                waterConsumptionViewModel.todayConsumption.collect { consumption ->
                    userViewModel.user.value?.let { user ->
                        updateProgressBar(user.dailyWaterNeed, consumption)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                waterConsumptionViewModel.weeklyConsumption.collect { weeklyData ->
                    setupLineChart(weeklyData)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                waterConsumptionViewModel.averageConsumption.collect { average ->
                    averageConsumptionText.text = "Среднее дневное потребление: ${average}мл"
                }
            }
        }
    }

    private fun updateProgressBar(dailyNeed: Int, currentConsumption: Int = 0) {
        todayProgressText.text = "${currentConsumption}мл / ${dailyNeed}мл"
        val progress = if (dailyNeed > 0) {
            (currentConsumption.toFloat() / dailyNeed * 100).toInt()
        } else {
            0
        }
        waterProgressBar.progress = progress
    }

    private fun setupLineChart(weeklyData: List<Pair<Long, Int>>) {
        val entries = weeklyData.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second.toFloat())
        }

        val dataSet = LineDataSet(entries, "Потребление воды")
        dataSet.color = Color.rgb(127, 140, 170)
        dataSet.valueTextColor = Color.rgb(51, 52, 70)
        dataSet.lineWidth = 2f

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun setupClickListeners() {
        accountButton.setOnClickListener {
            navigateToAccountActivity()
        }

        log250Button.setOnClickListener {
            waterConsumptionViewModel.logWater(250)
        }

        log500Button.setOnClickListener {
            waterConsumptionViewModel.logWater(500)
        }

        logCustomButton.setOnClickListener {
            showCustomAmountDialog()
        }
    }

    private fun showCustomAmountDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("Добавить потребление воды")
            .setMessage("Введите количество воды в мл:")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val amount = input.text.toString().toIntOrNull()
                amount?.let {
                    waterConsumptionViewModel.logWater(it)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToAccountActivity() {
        val intent = Intent(this, AccountActivity::class.java)
        startActivity(intent)
        finish()
    }
}

