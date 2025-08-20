package com.colman.dailypulse.features.habits

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.utils.LocalSnackbarController
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Habits(
    viewModel: HabitsViewModel = koinViewModel(),
    onCreateHabitClick: () -> Unit,
    onEditHabitClick: (Habit) -> Unit,
    onShareHabitSuccess: () -> Unit,
) {
    var currentIndex by remember { mutableStateOf(0) }
    val uiState = viewModel.uiState.collectAsState().value

    val snackbarController = LocalSnackbarController.current

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { msg ->
            snackbarController.showMessage(msg)
        }
    }

    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.dayOfWeek }
        when(uiState) {
            is HabitsState.Error -> ErrorContent(message = uiState.errorMessage)
            is HabitsState.Loaded -> HabitsContent(
                uiState.habits.items,
                currentIndex,
                onIndexChange = { currentIndex = it },
                today,
                onCreateHabitClick,
                onEditHabitClick = onEditHabitClick,
                onDeleteHabit = { habit -> viewModel.onDeleteHabit(habit.id?: "")},
                onHabitDone = {habit -> viewModel.onHabitDone(habit.id?: "")},
                onHabitShare = {habit -> viewModel.onShareHabit(habit, onShareHabitSuccess)}
            )
            HabitsState.Loading -> LoadingContent()
        }
    }

@Composable
fun HabitsContent(
    habits: List<Habit>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    today: DayOfWeek,
    onCreateHabitClick: () -> Unit,
    onEditHabitClick: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    onHabitDone: (Habit) -> Unit,
    onHabitShare: (Habit) -> Unit,
) {
    var showGoalReachedDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (habits.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Don't have habits yet?", style = MaterialTheme.typography.titleLarge)
                Text("Let's add your first one!", style = MaterialTheme.typography.titleLarge)
            }
        } else {
            val habit = habits.getOrNull(currentIndex) ?: return

            val titleSection = @Composable {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp)
                ) {
                    Text(habit.title ?: "", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Active on:\n${habit.daysOfWeek?.joinToString { it.name } ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val actionSection = @Composable {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp)
                ) {
                    if (habit.daysOfWeek?.contains(today) == true && habit.totalCount!! < (habit.goal ?: 60)) {
                        Button(onClick = {
                            val nextCount = (habit.totalCount ?: 0) + 1
                            if (nextCount >= (habit.goal ?: 60)) {
                                showGoalReachedDialog = true
                            }
                            onHabitDone(habit)
                        }) {
                            Text("Done")
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { onEditHabitClick(habit) }) {
                            Text("Edit")
                        }
                        Button(onClick = { onDeleteHabit(habit) }) {
                            Text("Delete")
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        habits.forEachIndexed { index, _ ->
                            val selected = index == currentIndex
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(if (selected) 12.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) Color.Black else Color.Gray)
                            )
                        }
                    }
                }
            }

            titleSection()
            Box(modifier = Modifier.align(Alignment.Center)) {
                DonutProgress(progress = habit.totalCount ?: 0, habit.goal ?: 60)
            }
            actionSection()

            if (currentIndex > 0) {
                IconButton(
                    onClick = { onIndexChange(currentIndex - 1) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }
            }

            if (currentIndex < habits.lastIndex) {
                IconButton(
                    onClick = { onIndexChange(currentIndex + 1) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }

            if (showGoalReachedDialog) {
                Dialog(onDismissRequest = { showGoalReachedDialog = false }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 8.dp,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { showGoalReachedDialog = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }

                            Text(
                                text = "🎉",
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "You have created a new habit!",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            )

                            Spacer(Modifier.height(24.dp))

                            Button(onClick = {
                                onHabitShare(habit)
                                showGoalReachedDialog = false
                            }) {
                                Text("Share")
                            }
                        }
                    }
                }
            }
            }

        FloatingActionButton(
            onClick = onCreateHabitClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Habit")
        }
    }
}

@Composable
fun DonutProgress(progress: Int, goal: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtMost(goal) / goal.toFloat(),
        animationSpec = tween(durationMillis = 600),
        label = "DonutProgress"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 20f)
            )
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 20f)
            )
        }

        Text(
            text = "${progress.coerceAtMost(goal)} / $goal",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ErrorContent(message: String) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = TextStyle(
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            trackColor = MaterialTheme.colorScheme.secondary
        )
    }
}