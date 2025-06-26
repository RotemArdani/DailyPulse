package com.colman.dailypulse.features.habits

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.features.BaseViewModel
import com.colman.dailypulse.features.posts.PostsState
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits
import com.colman.dailypulse.models.posts.Posts
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitsViewModel(
    private val useCases: HabitsUseCases,
): BaseViewModel() {
    private val _uiState: MutableStateFlow<HabitsState> =
        MutableStateFlow(HabitsState.Loading)
    val uiState: StateFlow<HabitsState> get() = _uiState

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage

    private var log = Logger.withTag("HabitsViewModel")

    init {
        fetchHabits()
    }

    private fun fetchHabits() {
        scope.launch {
            val result = useCases.getHabits()

            when (result) {
                is Result.Success -> {
                    _uiState.emit(
                        HabitsState.Loaded( result.data ?: Habits(items = emptyList()))
                    )
                }

                is Result.Failure -> {
                    _uiState.emit(
                        HabitsState.Error(errorMessage = result.error?.message ?: "Failed to fetch habits")
                    )
                }
            }
        }
    }


    fun onCreateHabit(habit: Habit) {
        scope.launch {
            _saveState.value = SaveState.Saving // Indicate saving is in progress

            // Call the repository's saveHabit function
            when (val result = useCases.createHabit(habit)) {
                is Result.Success -> {
                    _saveState.value = SaveState.Success
                    // Optionally, reload habits or update the UI to reflect the change
                    fetchHabits() // Or more sophisticated UI update
                }
                is Result.Failure -> {
                    _saveState.value = SaveState.Error(result.error?.message ?: "Failed to create habits")
                }
            }
        }
    }

    fun onHabitDone(habitId: String) {
        scope.launch {
        try {
            val current = _uiState.value
            if (current !is HabitsState.Loaded) return@launch

            val updatedItems = current.habits.items.map { habit ->
                if (habit.id == habitId) {
                    val result = useCases.onHabitDone(habitId)
                    if (result is Result.Failure) {
                        throw Exception("Failed to like post.")
                    }

                    habit.copy(totalCount = habit.totalCount?.plus(1))
                } else {
                    habit
                }
            }
            _uiState.value = HabitsState.Loaded(Habits(updatedItems))
        } catch (e: Exception) {
            _snackbarMessage.emit("Failed to like post.")
        }
        }
    }

    fun onUpdateHabit(habit: Habit) {
        scope.launch {
            _saveState.value = SaveState.Saving // Indicate saving is in progress

            when (val result = useCases.updateHabit(habit)) {
                is Result.Success -> {
                    _saveState.value = SaveState.Success
                    fetchHabits() // Or more sophisticated UI update
                }
                is Result.Failure -> {
                    _saveState.value = SaveState.Error(result.error?.message ?: "Failed to create habits")
                }
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}