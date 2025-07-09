package com.colman.dailypulse.features.habits

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.utils.LocalSnackbarController
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import org.koin.androidx.compose.koinViewModel
import java.time.format.TextStyle
import java.util.Locale
import com.colman.dailypulse.features.habits.DayOfWeekSelector


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditHabitScreen(
    habitId: String?,
    viewModel: HabitsViewModel = koinViewModel(),
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val saveState by viewModel.saveState.collectAsState()
    val snackbarController = LocalSnackbarController.current

    val habit = remember(uiState) {
        (uiState as? HabitsState.Loaded)?.habits?.items?.find { it.id == habitId }
    }

    var title by remember { mutableStateOf(habit?.title.orEmpty()) }
    var selectedDays by remember { mutableStateOf(habit?.daysOfWeek?.toSet() ?: emptySet()) }
    var frequencyInput by remember { mutableStateOf(habit?.frequency?.toString() ?: "1") }
    var frequencyError by remember { mutableStateOf<String?>(null) }
    var hasAttemptedSave by remember { mutableStateOf(false) }

    if (habit == null) {
        Text("Habit not found", modifier = Modifier.padding(24.dp))
        return
    }

    LaunchedEffect(saveState) {
        when (val current = saveState) {
            is SaveState.Success -> {
                snackbarController.showMessage("Habit updated successfully!")
                onSuccess()
            }
            is SaveState.Error -> {
                snackbarController.showMessage("Error: ${current.errorMessage}")
            }
            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (saveState !is SaveState.Success) {
                viewModel.resetSaveState()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Habit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            hasAttemptedSave = true
                            val frequency = frequencyInput.toIntOrNull()

                            if (frequency == null || frequency <= 0) {
                                frequencyError = "Must be a positive number"
                                return@IconButton
                            }

                            if (title.isNotBlank() && selectedDays.isNotEmpty()) {
                                val updatedHabit = habit.copy(
                                    title = title.trim(),
                                    frequency = frequency,
                                    daysOfWeek = selectedDays
                                )
                                viewModel.onUpdateHabit(updatedHabit)
                            } else {
                                snackbarController.showMessage("Please fill all required fields")
                            }
                        },
                        enabled = saveState !is SaveState.Saving
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Habit Title*") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank() && hasAttemptedSave
            )

            if (title.isBlank() && hasAttemptedSave) {
                Text(
                    "Title cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Repeat on Days*", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            DayOfWeekSelector(
                selectedDays = selectedDays,
                onDaySelected = { day, selected ->
                    selectedDays = if (selected) selectedDays + day else selectedDays - day
                }
            )

            if (selectedDays.isEmpty() && hasAttemptedSave) {
                Text(
                    "Please select at least one day.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = frequencyInput,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        frequencyInput = input
                        frequencyError = null
                    }
                },
                label = { Text("Times per period (e.g., 1)") },
                modifier = Modifier.fillMaxWidth(),
                isError = frequencyError != null
            )

            frequencyError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayButton(
    day: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).first().toString(),
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}