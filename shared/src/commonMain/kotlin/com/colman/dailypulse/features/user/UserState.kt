package com.colman.dailypulse.features.user

import com.colman.dailypulse.models.users.User

public sealed class UserState {
    data object Loading: UserState()
    data class Loaded(
        val user: User
    ): UserState()
    data class Error(
        var errorMessage: String
    ): UserState()
}