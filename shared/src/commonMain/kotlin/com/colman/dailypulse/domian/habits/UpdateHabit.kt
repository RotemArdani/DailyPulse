package com.colman.dailypulse.domian.habits

import com.colman.dailypulse.data.habits.HabitsRepository
import com.colman.dailypulse.models.habits.Habit

class UpdateHabit (
    private val habitsRepository: HabitsRepository
) {
    suspend operator fun invoke(habit: Habit) = habitsRepository.updateHabit(habit)
}