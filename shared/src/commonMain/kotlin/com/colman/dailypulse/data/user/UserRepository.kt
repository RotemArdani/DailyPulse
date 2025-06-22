package com.colman.dailypulse.data.user

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.models.users.User

interface UserRepository {
    suspend fun signIn(email: String, password: String): Result<User, Error>
    suspend fun signUp(email: String, password: String, name: String): Result<String, Error>
    suspend fun logout()
}