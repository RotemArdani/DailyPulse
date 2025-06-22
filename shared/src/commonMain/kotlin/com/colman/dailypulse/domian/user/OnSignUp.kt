package com.colman.dailypulse.domian.user

import com.colman.dailypulse.data.user.UserRepository

class OnSignUp (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String) = userRepository.signUp(email, password, name)
}