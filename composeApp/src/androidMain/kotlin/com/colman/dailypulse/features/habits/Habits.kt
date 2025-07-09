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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    onNavigateBack: () -> Unit
/*
    onDeleteHabitClick: (Habit) -> Unit
*/
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
                onDeleteHabit = { habit -> viewModel.onDeleteHabit(habit) {
                        onNavigateBack()
                    }
                },
                onHabitDone = {habit -> viewModel.onHabitDone(habit.id?: "")}
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
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (habits.isEmpty()) {
            // user has no habits
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Let's add your first habit!",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            // user has habits
            val habit = habits.getOrNull(currentIndex) ?: return

            val titleSection = @Composable {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                ) {
                    Text(
                        text = habit.title ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(12.dp))
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
                    if (habit.daysOfWeek?.contains(today) == true) {
                        Button(onClick = { habit.id?.let { onHabitDone(habit) } }) {
                            Text("Done")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
//
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
                DonutProgress(progress = habit.totalCount ?: 0)
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
fun DonutProgress(progress: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtMost(60) / 60f,
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
            text = "${(animatedProgress * 60).toInt()} / 60",
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