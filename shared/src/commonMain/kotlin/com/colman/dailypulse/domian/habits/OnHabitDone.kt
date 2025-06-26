package com.colman.dailypulse.domian.habits

import com.colman.dailypulse.data.habits.HabitsRepository

class OnHabitDone (
    private val habitsRepository: HabitsRepository
) {
    suspend operator fun invoke(habitId: String) = habitsRepository.habitDone(habitId)
}