package com.colman.dailypulse.data.firebase

import com.colman.dailypulse.models.Habit
import com.colman.dailypulse.models.Habits
import com.colman.dailypulse.models.Post
import com.colman.dailypulse.models.Posts

interface FirebaseRepository {
    suspend fun createHabit(habit: Habit)
    suspend fun createPost(post: Post)
    suspend fun getHabitDetails(habitId: String): Habit?
    suspend fun deleteHabit(habit: Habit)
    suspend fun getHabits(): Habits
    suspend fun getPosts(): Posts
    suspend fun signInAnonymously()
    suspend fun updateHabit(habit: Habit)
}