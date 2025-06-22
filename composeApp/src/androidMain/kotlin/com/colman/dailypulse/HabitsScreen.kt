package com.colman.dailypulse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.colman.dailypulse.features.habits.HabitsState
import com.colman.dailypulse.features.habits.HabitsViewModel
import com.colman.dailypulse.models.habits.Habit
import com.colman.dailypulse.models.habits.Habits
import org.koin.androidx.compose.koinViewModel

@Composable
fun HabitsScreen(viewModel: HabitsViewModel = koinViewModel()) {
    val uiState = viewModel.uiState.collectAsState().value

    when(uiState) {
        is HabitsState.Error -> ErrorContent(message = uiState.errorMessage)
        is HabitsState.Loaded -> HabitsGridContent(
            uiState.habits
        )
        HabitsState.Loading -> LoadingContent()
    }
}

@Composable
fun HabitsGridContent(
    habits: Habits,
    lazyListState: LazyGridState = rememberLazyGridState(),
    spacing: Dp = 4.dp,
//    onMovieClicked: (Movie) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        columns = GridCells.Fixed(count = 3),
        state = lazyListState,
        contentPadding = PaddingValues(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items(habits.items) { habit ->
            HabitGridContent(
                habit = habit,
//                onClick = { onMovieClicked(movie) }
            )
        }
    }
}

@Composable
fun HabitGridContent(
    modifier: Modifier = Modifier,
    habit: Habit,
//    onClick: () -> Unit
) {
    Card(
        modifier = modifier.size(250.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(contentColor = Color.LightGray),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
//        onClick = onClick
    ) {

        habit.title?.let {
            Text(
                text = it
            )
        }
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