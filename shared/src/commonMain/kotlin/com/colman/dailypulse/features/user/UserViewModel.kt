package com.colman.dailypulse.features.user

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.features.BaseViewModel
import com.colman.dailypulse.features.habits.SaveState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val useCases: UserUseCases,
): BaseViewModel() {
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private var log = Logger.withTag("UserViewModel")

    fun onSignIn(email: String, password: String) {
        scope.launch {
            _saveState.value = SaveState.Saving

            when (val result = useCases.signIn(email, password)) {
                is Result.Success -> {
                    _saveState.value = SaveState.Success
                }
                is Result.Failure -> {
                    _saveState.value = SaveState.Error(result.error?.message ?: "Unknown error")
                }
            }
        }
    }

    fun onSignUp(email: String, password: String, name: String) {
        scope.launch {
            _saveState.value = SaveState.Saving

            when (val result = useCases.signUp(email, password, name)) {
                is Result.Success -> {
                    _saveState.value = SaveState.Success
                }
                is Result.Failure -> {
                    _saveState.value = SaveState.Error(result.error?.message ?: "Unknown error")
                }
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}