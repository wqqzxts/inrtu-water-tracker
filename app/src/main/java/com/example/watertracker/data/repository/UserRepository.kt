package com.example.watertracker.data.repository

import com.example.watertracker.data.model.User
import com.example.watertracker.data.dao.UserDao

class UserRepository(private val userDao: UserDao) {
    fun insertUser(user: User) = userDao.insertUser(user)
    fun updateUser(user: User) = userDao.updateUser(user)
    fun getUser(): User? = userDao.getUser()
    fun userExists(): Boolean = userDao.userExists()
}