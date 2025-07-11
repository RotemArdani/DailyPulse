package com.colman.dailypulse.features.habits

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.colman.dailypulse.utils.LocalSnackbarController
import org.koin.androidx.compose.koinViewModel


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
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Edit Habit", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
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
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Habit Title*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        isError = title.isBlank() && hasAttemptedSave
                    )

                    if (title.isBlank() && hasAttemptedSave) {
                        Text(
                            "Title cannot be empty.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Frequency (times per day/period)*", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        isError = frequencyError != null
                    )

                    frequencyError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
