package com.example.watertracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

import com.example.watertracker.R
import com.example.watertracker.data.dao.DatabaseHelper
import com.example.watertracker.data.dao.UserDao
import com.example.watertracker.data.model.User
import com.example.watertracker.data.repository.UserRepository
import com.example.watertracker.util.WaterDailyNeedCalculator
import com.example.watertracker.viewmodel.UserViewModel
import com.example.watertracker.viewmodel.UserViewModelFactory

class AccountActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        dbHelper = DatabaseHelper(this@AccountActivity)

        initViews()
        setupViewModel()
        setupObservers()
    }

    private fun initViews() {
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        weightEditText = findViewById(R.id.weightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        ageEditText = findViewById(R.id.ageEditText)
        updateButton = findViewById(R.id.registerButton)

        updateButton.setOnClickListener {
            updateUser()
        }
    }

    private fun setupViewModel() {
        val userDao = UserDao(dbHelper)
        val userRepository = UserRepository(userDao)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(userRepository))[UserViewModel::class.java]
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.user.collect() { user ->
                    displayUserData(user)
                }
            }
        }
    }

    private fun updateUser() {
        val isMale = findViewById<RadioButton>(R.id.maleRadio).isChecked
        val weightText = weightEditText.text.toString()
        val heightText = heightEditText.text.toString()
        val ageText = ageEditText.text.toString()

        if (weightText.isEmpty() || heightText.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val weight = weightText.toDouble()
        val height = heightText.toDouble()
        val age = ageText.toInt()

        userViewModel.putUser(
            User(
                isMale = isMale,
                weight = weight,
                height = height,
                age = age,
                dailyWaterNeed = WaterDailyNeedCalculator.calculateDailyWaterNeed(weight, age)
            )
        )

        Toast.makeText(this@AccountActivity, "Данные успешно сохранены!", Toast.LENGTH_SHORT).show()
    }

    private fun displayUserData(user: User?) {
        user ?: return

        if (user.isMale) genderRadioGroup.check(R.id.maleRadio) else genderRadioGroup.check(R.id.femaleRadio)
        weightEditText.setText(user.weight.toString())
        heightEditText.setText(user.height.toString())
        ageEditText.setText(user.age.toString())
    }
}