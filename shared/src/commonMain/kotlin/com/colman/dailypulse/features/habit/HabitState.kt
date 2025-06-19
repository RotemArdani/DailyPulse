package com.colman.dailypulse.features.habit

import com.colman.dailypulse.models.Habit

public sealed class HabitState {
    data object Loading: HabitState()
    data class Loaded(
        val habit: Habit
    ): HabitState()
    data class Error(
        var errorMessage: String
    ): HabitState()
}