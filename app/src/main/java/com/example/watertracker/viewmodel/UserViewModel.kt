package com.example.watertracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.example.watertracker.data.model.User
import com.example.watertracker.data.repository.UserRepository

class UserViewModel(private val repository: UserRepository): ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _userExists = MutableStateFlow(false)
    val userExists: StateFlow<Boolean> = _userExists

    init {
        checkUserExists()
        loadUser()
    }

    fun putUser(user: User) {
        viewModelScope.launch {
            if (repository.userExists()) {
                repository.updateUser(user)
            } else {
                repository.insertUser(user)
            }
            _user.value = user
            _userExists.value = true
        }
    }

    private fun checkUserExists() {
        viewModelScope.launch {
            _userExists.value = repository.userExists()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = repository.getUser()
        }
    }
}