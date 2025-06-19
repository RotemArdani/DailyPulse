package com.colman.dailypulse.features.habit

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.habit.HabitRepository
import com.colman.dailypulse.features.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HabitViewModel(
    private val respository: HabitRepository,
    private val firebaseRepository: FirebaseRepository
): BaseViewModel() {
    private val _uiState: MutableStateFlow<HabitState> =
        MutableStateFlow(HabitState.Loading)
    val uiState: StateFlow<HabitState> get() = _uiState

    private var log = Logger.withTag("HabitsViewModel")

}