package com.colman.dailypulse.data.firebase

import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits
import com.colman.dailypulse.models.posts.Post
import com.colman.dailypulse.models.posts.Posts
import com.colman.dailypulse.models.users.User

interface FirebaseRepository {
    suspend fun getHabits(): Habits
    suspend fun createHabit(habit: Habit)
    suspend fun deleteHabit(habitId: String)
    suspend fun updateHabit(habit: Habit)
    suspend fun habitDone(habitId: String)
    suspend fun getHabitDetails(habitId: String): Habit?

    suspend fun getPosts(): Posts
    suspend fun createPost(post: Post)
    suspend fun likePost(postId: String)
    suspend fun deletePost(postId: String)

    suspend fun signInAnonymously()
    suspend fun logout()

    suspend fun signUpUser(email: String, password: String, name: String)
    suspend fun signInUser(email: String, password: String): User?
}