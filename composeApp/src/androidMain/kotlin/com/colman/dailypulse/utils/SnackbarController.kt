package com.colman.dailypulse.utils

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SnackbarController(private val snackbarHostState: SnackbarHostState) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}