package com.colman.dailypulse.features.habits

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.colman.dailypulse.features.habit.HabitState
import com.colman.dailypulse.features.habit.HabitViewModel
import com.colman.dailypulse.utils.LocalSnackbarController
import kotlinx.datetime.DayOfWeek
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditHabitScreen(
    habitId: String,
    viewModel: HabitViewModel = koinViewModel(parameters = { parametersOf(habitId) }),
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val habitState by viewModel.habitState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val snackbarController = LocalSnackbarController.current

    var title by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(emptySet<DayOfWeek>()) }
    var frequencyInput by remember { mutableStateOf("1") }
    var frequencyError by remember { mutableStateOf<String?>(null) }
    var hasAttemptedSave by remember { mutableStateOf(false) }

    LaunchedEffect(habitState) {
        if (habitState is HabitState.Loaded) {
            val habit = (habitState as HabitState.Loaded).habit
            title = habit?.title ?: ""
            selectedDays = habit?.daysOfWeek?.toSet() ?: emptySet()
            frequencyInput = habit?.frequency.toString()
        }
    }

    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveState.Success -> {
                snackbarController.showMessage("Habit updated successfully!")
                onSuccess()
            }
            is SaveState.Error -> {
                snackbarController.showMessage("Error: ${(saveState as SaveState.Error).errorMessage}")
            }
            else -> Unit
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetSaveState() }
    }

    when (habitState) {
        HabitState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is HabitState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Habit not found or failed to load", textAlign = TextAlign.Center)
            }
        }

        is HabitState.Loaded -> {
            val habit = (habitState as HabitState.Loaded).habit

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
                                        if (habit != null) {
                                            viewModel.updateHabit(
                                                habit.copy(
                                                    title = title.trim(),
                                                    frequency = frequency,
                                                    daysOfWeek = selectedDays
                                                )
                                            )
                                        }
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
                                    selectedDays =
                                        if (selected) selectedDays + day else selectedDays - day
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
                            Text(
                                "Frequency (times per day/period)*",
                                style = MaterialTheme.typography.titleMedium
                            )
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
    }
}