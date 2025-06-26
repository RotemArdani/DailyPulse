package com.colman.dailypulse.data.posts

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.habits.HabitsError
import com.colman.dailypulse.models.posts.Post
import com.colman.dailypulse.models.posts.Posts

data class PostsError (
    override val message: String
): Error

class RemotePostsRepository(
    private val firebaseRepository: FirebaseRepository
) : PostsRepository {
    override suspend fun getPosts(): Result<Posts, Error> {
        return try {
            val posts = firebaseRepository.getPosts();
            Result.Success(posts)
        } catch (e: Exception) {
            Result.Failure(
                PostsError(message = e.message ?: "")
            )
        }
    }

    override suspend fun createPost(post: Post): Result<String, Error> {
        return try {
            firebaseRepository.createPost(post);
            Result.Success("")
        } catch (e: Exception) {
            Result.Failure(
                HabitsError(message = e.message ?: "")
            )
        }
    }

    override suspend fun likePost(postId: String): Result<String, Error> {
        return try {
            firebaseRepository.likePost(postId);
            Result.Success("")
        } catch (e: Exception) {
            Result.Failure(
                HabitsError(message = e.message ?: "")
            )
        }
    }
}