package com.colman.dailypulse.domian.user

import com.colman.dailypulse.data.user.UserRepository

class OnLogout (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.logout()
}
