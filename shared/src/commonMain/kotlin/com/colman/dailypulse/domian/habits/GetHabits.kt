package com.colman.dailypulse.domian.habits

import com.colman.dailypulse.data.habits.HabitsRepository

class GetHabits (
    private val habitsRepository: HabitsRepository
) {
    suspend operator fun invoke() = habitsRepository.getHabits()
}