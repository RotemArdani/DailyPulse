package com.colman.dailypulse.features.habits

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.habits.HabitsRepository
import com.colman.dailypulse.features.BaseViewModel
import com.colman.dailypulse.models.Habit
import com.colman.dailypulse.models.Habits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitsViewModel(
    private val respository: HabitsRepository,
    private val firebaseRepository: FirebaseRepository
): BaseViewModel() {
    private val _uiState: MutableStateFlow<HabitsState> =
        MutableStateFlow(HabitsState.Loading)
    val uiState: StateFlow<HabitsState> get() = _uiState

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private var log = Logger.withTag("HabitsViewModel")

    init {
        fetchHabits()
        signin()
    }

    private fun signin() {
        scope.launch {
            firebaseRepository.signInAnonymously()
        }
    }

    private fun fetchHabits() {
        scope.launch {
            val result = respository.getHabits()

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

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    private fun onCreateHabit(habit: Habit) {
        scope.launch {
            _saveState.value = SaveState.Saving // Indicate saving is in progress

            // Call the repository's saveHabit function
            when (val result = respository.createHabit(habit)) {
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

    private fun onUpdateHabit(habit: Habit) {
        scope.launch {
            _saveState.value = SaveState.Saving // Indicate saving is in progress

            // Call the repository's saveHabit function
            when (val result = respository.updateHabit(habit)) {
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
}