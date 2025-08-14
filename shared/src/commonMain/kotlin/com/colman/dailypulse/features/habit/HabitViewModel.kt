package com.colman.dailypulse.features.habit

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.habit.HabitRepository
import com.colman.dailypulse.features.BaseViewModel
import com.colman.dailypulse.features.habits.HabitsState
import com.colman.dailypulse.features.habits.HabitsUseCases
import com.colman.dailypulse.features.habits.SaveState
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitViewModel(
    private val useCases: HabitsUseCases,
    private val habitId: String?
) : BaseViewModel() {


    private val _habitState = MutableStateFlow<HabitState>(HabitState.Loading)
    val habitState: StateFlow<HabitState> get() = _habitState

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage

    init {
        if (habitId != null) fetchHabitDetails(habitId)
    }

    private fun fetchHabitDetails(id: String) {
        scope.launch {
            val result = useCases.getHabitDetails(id)
            when (result) {
                is Result.Success -> _habitState.emit(
                    HabitState.Loaded(result.data))
                is Result.Failure -> _habitState.emit(
                    HabitState.Error(result.error?.message ?: "Failed to fetch habit")
                )
            }
        }
    }

    fun createHabit(habit: Habit) {
        scope.launch {
            _saveState.value = SaveState.Saving
            when (val result = useCases.createHabit(habit)) {
                is Result.Success -> _saveState.value = SaveState.Success
                is Result.Failure -> _saveState.value =
                    SaveState.Error(result.error?.message ?: "Failed to create habit")
            }
        }
    }

    fun updateHabit(habit: Habit) {
        scope.launch {
            _saveState.value = SaveState.Saving
            when (val result = useCases.updateHabit(habit)) {
                is Result.Success -> _saveState.value = SaveState.Success
                is Result.Failure -> _saveState.value =
                    SaveState.Error(result.error?.message ?: "Failed to update habit")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}
