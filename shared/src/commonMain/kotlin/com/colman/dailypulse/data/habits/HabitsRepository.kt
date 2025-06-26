package com.colman.dailypulse.data.habits

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits

interface HabitsRepository {
    suspend fun getHabits(): Result<Habits, Error>
    suspend fun createHabit(habit: Habit): Result<String, Error>
    suspend fun updateHabit(habit: Habit): Result<String, Error>
    suspend fun habitDone(habitId: String): Result<String, Error>
}