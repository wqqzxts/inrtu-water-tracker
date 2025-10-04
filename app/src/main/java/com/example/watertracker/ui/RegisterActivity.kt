package com.example.watertracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.watertracker.R
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast

import android.widget.RadioGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton

import com.example.watertracker.data.dao.DatabaseHelper
import com.example.watertracker.data.dao.UserDao
import com.example.watertracker.data.model.User
import com.example.watertracker.data.repository.UserRepository
import com.example.watertracker.util.WaterDailyNeedCalculator
import com.example.watertracker.viewmodel.UserViewModel
import com.example.watertracker.viewmodel.UserViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this@RegisterActivity)

        initViews()
        setupViewModel()
    }

    private fun initViews() {
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        weightEditText = findViewById(R.id.weightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        ageEditText = findViewById(R.id.ageEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun setupViewModel() {
        val userDao = UserDao(dbHelper)
        val userRepository = UserRepository(userDao)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(userRepository))[UserViewModel::class.java]
    }

    private fun registerUser() {
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
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}