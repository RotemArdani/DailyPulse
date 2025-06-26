package com.colman.dailypulse.features.posts

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.colman.dailypulse.features.habits.SaveState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreatePostScreen(viewModel: PostsViewModel = koinViewModel(), onNavigateBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val description by viewModel.description.collectAsState()

    val imageUrl by viewModel.imageUrl.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bytes = context.contentResolver.openInputStream(it)?.readBytes()
                bytes?.let { viewModel.uploadImage(it) }
            }
        }

    LaunchedEffect(saveState) {
        when (val currentSaveState = saveState) {
            is SaveState.Success -> {
                snackbarHostState.showSnackbar("Post created successfully!")
                viewModel.resetSaveState()
                onSuccess()
            }

            is SaveState.Error -> {
                snackbarHostState.showSnackbar("Error: ${currentSaveState.errorMessage}")
            }

            else -> { /* Idle or Saving */
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
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Select Image")
            }

            imageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Uploaded image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Button(
                onClick = { viewModel.onCreatePost() },
                modifier = Modifier.align(Alignment.End),
                enabled = saveState !is SaveState.Saving
            ) {
                Text("Create Post")
            }
        }
    }
}