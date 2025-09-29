package com.example.watertracker.data.repository

import com.example.watertracker.data.model.User
import com.example.watertracker.data.dao.UserDao

import java.util.Date

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun getUser(): User? = userDao.getUser()
    suspend fun userExists(): Boolean = userDao.userExists()
}