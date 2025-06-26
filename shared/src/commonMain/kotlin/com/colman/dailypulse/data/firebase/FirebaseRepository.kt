package com.colman.dailypulse.data.firebase

import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits
import com.colman.dailypulse.models.posts.Post
import com.colman.dailypulse.models.posts.Posts
import com.colman.dailypulse.models.users.User

interface FirebaseRepository {
    suspend fun getHabits(): Habits
    suspend fun createHabit(habit: Habit)
    suspend fun getHabitDetails(habitId: String): Habit?
    suspend fun deleteHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun habitDone(habitId: String)

    suspend fun getPosts(): Posts
    suspend fun createPost(post: Post)
    suspend fun likePost(postId: String)

    suspend fun signInAnonymously()

    suspend fun signUpUser(email: String, password: String, name: String)
    suspend fun signInUser(email: String, password: String): User?
}