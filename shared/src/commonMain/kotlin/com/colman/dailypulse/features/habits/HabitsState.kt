package com.colman.dailypulse.features.habits

import com.colman.dailypulse.models.habits.Habits

public sealed class HabitsState {
    data object Loading: HabitsState()
    data class Loaded(
        val habits: Habits
    ): HabitsState()
    data class Error(
        var errorMessage: String
    ): HabitsState()
}

sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data object Success : SaveState
    data class Error(val errorMessage: String) : SaveState
}