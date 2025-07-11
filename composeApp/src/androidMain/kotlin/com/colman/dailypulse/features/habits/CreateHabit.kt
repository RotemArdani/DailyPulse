package com.colman.dailypulse.features.habits

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateHabitScreen(
    viewModel: HabitsViewModel = koinViewModel(),
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(emptySet<DayOfWeek>()) }
    var frequencyInput by remember { mutableStateOf("1") }
    var frequencyError by remember { mutableStateOf<String?>(null) }
    val saveState by viewModel.saveState.collectAsState()
    val snackbarController = LocalSnackbarController.current;
    var hasAttemptedSave by remember { mutableStateOf(false) }

    LaunchedEffect(saveState) {
        when (val currentSaveState = saveState) {
            is SaveState.Success -> {
                snackbarController.showMessage("Habit created successfully!")
                title = ""
                selectedDays = emptySet()
                hasAttemptedSave = false
                frequencyError = "1"
                onSuccess()
            }
            is SaveState.Error -> {
                snackbarController.showMessage("Error: ${currentSaveState.errorMessage}")
            }
            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose { if (saveState !is SaveState.Success) viewModel.resetSaveState() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Create New Habit", style = MaterialTheme.typography.titleLarge)
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
                    IconButton(onClick = {
                        hasAttemptedSave = true
                        val currentFrequency = frequencyInput.toIntOrNull()

                        if (currentFrequency == null || currentFrequency <= 0) {
                            frequencyError = "Frequency must be a positive number."
                            snackbarController.showMessage("Please correct the errors.")
                            return@IconButton
                        } else {
                            frequencyError = null
                        }

                        if (title.isNotBlank() && selectedDays.isNotEmpty() && frequencyError == null) {
                            viewModel.onCreateHabit(
                                Habit(
                                    id = null,
                                    title = title.trim(),
                                    daysOfWeek = selectedDays,
                                    frequency = currentFrequency,
                                    dailyCount = 0,
                                    totalCount = 0,
                                )
                            )
                        } else {
                            snackbarController.showMessage("Title and at least one day are required.")
                        }
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = "Save Habit")
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
                Column(Modifier.padding(16.dp)) {
                    Text("Habit Title*", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Enter a name...") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = title.isBlank() && hasAttemptedSave,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )
                    if (title.isBlank() && hasAttemptedSave) {
                        Text("Title cannot be empty.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Repeat on Days*", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(12.dp))
                    DayOfWeekSelector(
                        selectedDays = selectedDays,
                        onDaySelected = { day, isSelected ->
                            selectedDays = if (isSelected) selectedDays + day else selectedDays - day
                        }
                    )
                    if (selectedDays.isEmpty() && hasAttemptedSave) {
                        Text("Please select at least one day.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Frequency*", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = frequencyInput,
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() }) frequencyInput = it
                        },
                        placeholder = { Text("Times per day, e.g. 1") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = frequencyError != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                    if (frequencyError != null) {
                        Text(frequencyError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}


/*
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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateHabitScreen(
    viewModel: HabitsViewModel = koinViewModel(),
    onSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(emptySet<DayOfWeek>()) }
    var frequencyInput by remember { mutableStateOf("1") }
    var frequencyError by remember { mutableStateOf<String?>(null) }

    val saveState by viewModel.saveState.collectAsState()
    val snackbarController = LocalSnackbarController.current;

    var hasAttemptedSave by remember { mutableStateOf(false) } // To show validation

    LaunchedEffect(saveState) {
        when (val currentSaveState = saveState) {
            is SaveState.Success -> {
                snackbarController.showMessage("Habit created successfully!")
                title = ""
                selectedDays = emptySet()
                hasAttemptedSave = false
                frequencyError = "1"
                onSuccess()
            }
            is SaveState.Error -> {
                snackbarController.showMessage("Error: ${currentSaveState.errorMessage}")
            }
            else -> {
*//* Idle or Saving *//*
 }
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
                                    snackbarController.showMessage("Please correct the errors.")
                                }
                                return@IconButton
                            } else {
                                frequencyError = null
                            }


                            if (title.isNotBlank() && selectedDays.isNotEmpty() && frequencyError == null) {
                                val newHabit = Habit(
                                    id = null,
                                    title = title.trim(),
                                    daysOfWeek = selectedDays,
                                    frequency = currentFrequency,
                                    dailyCount = 0,
                                    totalCount = 0,
                                )
                                viewModel.onCreateHabit(newHabit)
                            } else {
                                kotlinx.coroutines.MainScope().launch {
                                    snackbarController.showMessage("Title and at least one day are required.")
                                }
                            }
                        },
                        enabled = saveState !is SaveState.Saving
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
                isError = title.isBlank() && hasAttemptedSave
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
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Text("Frequency (times per day/period)*", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = frequencyInput,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        frequencyInput = newValue
                        frequencyError = if ((newValue.toIntOrNull() ?: 0) <= 0) {
                            "Must be a positive number"
                        } else {
                            null
                        }
                    } else if (newValue.isEmpty()) {
                        frequencyInput = ""
                        frequencyError = "Frequency cannot be empty"
                    }
                },
                label = { Text("Times per period (e.g., 1)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
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

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDaySelected: (DayOfWeek, Boolean) -> Unit
) {
    val days = remember {
        listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
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
