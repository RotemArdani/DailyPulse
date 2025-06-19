package com.colman.dailypulse.features.habits

import com.colman.dailypulse.models.Habits

public sealed class HabitsState {
    data object Loading: HabitsState()
    data class Loaded(
        val habits: Habits
    ): HabitsState()
    data class Error(
        var errorMessage: String
    ): HabitsState()
}

//public sealed class SaveHabitState {
//    data object Idle : SaveHabitState()
//    data object Saving : SaveHabitState()
//    data object Success : SaveHabitState()
//    data class Error(val errorMessage: String) : SaveHabitState()
//}

sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data object Success : SaveState
    data class Error(val errorMessage: String) : SaveState
}