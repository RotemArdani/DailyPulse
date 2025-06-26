package com.colman.dailypulse.data.habits

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits
import kotlinx.serialization.Serializable

data class HabitsError (
    override val message: String
): Error

class RemoteHabitsRepository(
  private val firebaseRepository: FirebaseRepository
) : HabitsRepository {
    override suspend fun getHabits(): Result<Habits, Error> {
        return try {
            val habits = firebaseRepository.getHabits();
            Result.Success(habits)
        } catch (e: Exception) {
            Result.Failure(
                HabitsError(message = e.message ?: "")
            )
        }
    }

    override suspend fun createHabit(habit: Habit): Result<String, Error> {
        return try {
            firebaseRepository.createHabit(habit);
            Result.Success("")
        } catch (e: Exception) {
            Result.Failure(
                HabitsError(message = e.message ?: "")
            )
        }
    }

    override suspend fun updateHabit(habit: Habit): Result<String, Error> {
        return try {
            firebaseRepository.updateHabit(habit);
            Result.Success("")
        } catch (e: Exception) {
            Result.Failure(
                HabitsError(message = e.message ?: "")
            )
        }
    }

    override suspend fun habitDone(habitId: String): Result<String, Error> {
        return try {
            firebaseRepository.habitDone(habitId);
            Result.Success("")
        } catch (e: Exception) {
            Result.Failure(
                HabitsError(message = e.message ?: "")
            )
        }
    }
}

@Serializable
data class HabitsResponse(
    val results: List<Habit>
)