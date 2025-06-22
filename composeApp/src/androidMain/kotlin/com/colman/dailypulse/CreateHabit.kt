package com.colman.dailypulse

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
import com.colman.dailypulse.features.habits.HabitsViewModel
import com.colman.dailypulse.features.habits.SaveState
import com.colman.dailypulse.models.habits.Habit
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import org.koin.androidx.compose.koinViewModel
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateHabitScreen(
    // Assuming HabitsViewModel is used. If you have a dedicated CreateHabitViewModel, use that.
    viewModel: HabitsViewModel = koinViewModel(),
//    onHabitCreatedSuccessfully: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(emptySet<DayOfWeek>()) }
    var frequencyInput by remember { mutableStateOf("1") }
    var frequencyError by remember { mutableStateOf<String?>(null) }

    val saveState by viewModel.saveState.collectAsState() // Observe save operation state
    val snackbarHostState = remember { SnackbarHostState() }

    var hasAttemptedSave by remember { mutableStateOf(false) } // To show validation errors after first try

    // Handle save state changes
    LaunchedEffect(saveState) {
        when (val currentSaveState = saveState) {
            is SaveState.Success -> {
                snackbarHostState.showSnackbar("Habit created successfully!")
                // Reset local screen state if needed, though usually navigation handles this
                title = ""
                selectedDays = emptySet()
                hasAttemptedSave = false
                frequencyError = "1"
                // ViewModel state should also be reset by the ViewModel or before navigating
                // viewModel.resetSaveHabitState() // Ensure this is called
//                onHabitCreatedSuccessfully()
            }
            is SaveState.Error -> {
                snackbarHostState.showSnackbar("Error: ${currentSaveState.errorMessage}")
                // ViewModel should ideally reset its state to allow another attempt.
                // viewModel.resetSaveHabitState()
            }
            else -> { /* Idle or Saving */ }
        }
    }

    // Reset ViewModel's save state when the screen is disposed if not successful
    DisposableEffect(Unit) {
        onDispose {
            if (saveState !is SaveState.Success) {
                viewModel.resetSaveState()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create New Habit") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer, // Example
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer // Example
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
                            val currentFrequency = frequencyInput.toIntOrNull()

                            if (currentFrequency == null || currentFrequency <= 0) {
                                frequencyError = "Frequency must be a positive number."
                                // Also show a snackbar for general validation failure
                                kotlinx.coroutines.MainScope().launch {
                                    snackbarHostState.showSnackbar("Please correct the errors.")
                                }
                                return@IconButton // Stop the save process
                            } else {
                                frequencyError = null // Clear error if valid now
                            }


                            if (title.isNotBlank() && selectedDays.isNotEmpty() && frequencyError == null) {
                                val newHabit = Habit(
                                    id = null,
                                    title = title.trim(),
                                    daysOfWeek = selectedDays,
                                    frequency = currentFrequency, // Use the parsed frequency
                                    dailyCount = 0,
                                    totalCount = 0,
                                )
                                viewModel.onCreateHabit(newHabit)
                            } else {
                                // Validation error feedback is handled by conditional Text below fields
                                // and the Snackbar on first validation failure.
                                kotlinx.coroutines.MainScope().launch { // Use MainScope for quick UI feedback
                                    snackbarHostState.showSnackbar("Title and at least one day are required.")
                                }
                            }
                        },
                        enabled = saveState !is SaveState.Saving // Disable while saving
                    )
                    { Icon(Icons.Filled.Check, contentDescription = "Save Habit") }
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
            // Habit Title
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
                isError = title.isBlank() && hasAttemptedSave // Show error if blank after trying to save
            )
            if (title.isBlank() && hasAttemptedSave) {
                Text(
                    "Title cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Days of the Week Selector
            Text("Repeat on Days*", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            DayOfWeekSelector(
                selectedDays = selectedDays,
                onDaySelected = { day, isSelected ->
                    selectedDays = if (isSelected) {
                        selectedDays + day
                    } else {
                        selectedDays - day
                    }
                }
            )
            if (selectedDays.isEmpty() && hasAttemptedSave) {
                Text(
                    "Please select at least one day.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp) // Complete this line
                )
            }

            Text("Frequency (times per day/period)*", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = frequencyInput,
                onValueChange = { newValue ->
                    // Allow only digits and ensure it's not empty, or handle empty as error
                    if (newValue.all { it.isDigit() }) {
                        frequencyInput = newValue
                        // Basic validation: ensure it's a positive number when attempting save
                        // More robust validation can be done on save attempt
                        frequencyError = if (newValue.toIntOrNull() ?: 0 <= 0) {
                            "Must be a positive number"
                        } else {
                            null
                        }
                    } else if (newValue.isEmpty()) {
                        frequencyInput = "" // Allow clearing the field
                        frequencyError = "Frequency cannot be empty"
                    }
                },
                label = { Text("Times per period (e.g., 1)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done // Or ImeAction.Next if more fields follow
                ),
                isError = (frequencyInput.isBlank() && hasAttemptedSave) || frequencyError != null
            )
            if ((frequencyInput.isBlank() && hasAttemptedSave)) {
                Text(
                    "Frequency cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            } else if (frequencyError != null) {
                Text(
                    text = frequencyError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Spacer after this section

            Spacer(modifier = Modifier.height(24.dp)) // Add some space at the bottom

            // You could add more fields here like:
            // - Frequency (Daily, Specific Days, X times per week)
            // - Daily Target (e.g., number of times, minutes)
            // - Reminders
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDaySelected: (DayOfWeek, Boolean) -> Unit
) {
    // Get all days of the week, starting from Monday to Sunday for common representation
    val days = remember {
        listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround // Distribute space evenly
    ) {
        days.forEach { day ->
            val isSelected = selectedDays.contains(day)
            DayButton(
                day = day,
                isSelected = isSelected,
                onClick = { onDaySelected(day, !isSelected) }
            )
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
            .size(40.dp) // Fixed size for a circular button
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).first().toString(), // e.g., "M"
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}