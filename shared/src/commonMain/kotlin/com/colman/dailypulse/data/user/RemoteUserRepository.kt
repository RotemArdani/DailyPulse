package com.colman.dailypulse.data.user

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.posts.PostsError
import com.colman.dailypulse.models.users.User

class RemoteUserRepository(
    private val firebaseRepository: FirebaseRepository
) : UserRepository {
    private val log = Logger.withTag("RemoteUserRepository")
    override suspend fun signIn(email: String, password: String): Result<User, Error> {
        return try {
            val result = firebaseRepository.signInUser(email, password);
            Result.Success(result)
        } catch (e: Exception) {
            Result.Failure(
                PostsError(message = e.message ?: "")
            )
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): Result<String, Error> {
        return try {
            firebaseRepository.signUpUser(email, password, name);
            Result.Success("")
        } catch (e: Exception) {
            Result.Failure(
                PostsError(message = e.message ?: "")
            )
        }
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

}