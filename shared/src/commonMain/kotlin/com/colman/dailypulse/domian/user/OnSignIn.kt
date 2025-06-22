package com.colman.dailypulse.domian.user

import com.colman.dailypulse.data.user.UserRepository

class OnSignIn (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String) = userRepository.signIn(email, password)
}
