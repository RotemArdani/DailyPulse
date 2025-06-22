package com.colman.dailypulse.data.habit

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.models.habits.Habit

interface HabitRepository {
    suspend fun getHabit(): Result<Habit, Error>
}