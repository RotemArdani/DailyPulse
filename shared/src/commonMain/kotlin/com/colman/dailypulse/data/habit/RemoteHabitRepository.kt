package com.colman.dailypulse.data.habit

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.habits.HabitsRepository
import com.colman.dailypulse.models.Habit
import io.ktor.client.HttpClient

class RemoteHabitRepository(
    private val client: HttpClient,
    private val bearerToken: String
) : HabitRepository {
    override suspend fun getHabit(): Result<Habit, Error> {
        TODO("Not yet implemented")
    }
}