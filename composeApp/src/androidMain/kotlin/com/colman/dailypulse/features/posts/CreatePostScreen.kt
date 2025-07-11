package com.colman.dailypulse.features.posts

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.colman.dailypulse.features.habits.SaveState
import com.colman.dailypulse.utils.LocalSnackbarController
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreatePostScreen(
    viewModel: PostsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val description by viewModel.description.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val snackbarController = LocalSnackbarController.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            bytes?.let { viewModel.uploadImage(it) }
        }
    }

    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveState.Success -> {
                snackbarController.showMessage("Post created successfully!")
                viewModel.resetSaveState()
                onSuccess()
            }

            is SaveState.Error -> {
                snackbarController.showMessage("Unable to create post")
            }

            else -> Unit
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (saveState !is SaveState.Success) {
                viewModel.resetSaveState()
            }
        }
    }

    val transformedImageUrl = imageUrl?.let {
        val parts = it.split("/upload/")
        if (parts.size == 2) {
            "${parts[0]}/upload/c_pad,b_gray,w_800,h_800/${parts[1]}"
        } else it
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
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

            // Image picker card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl == null) {
                    Text("Tap to select image", color = Color.Gray)
                } else {
                    AsyncImage(
                        model = transformedImageUrl,
                        contentDescription = "Uploaded image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Delete button under image if present
            if (imageUrl != null) {
                TextButton(
                    onClick = { viewModel.clearImage() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete image")
                    Spacer(Modifier.width(4.dp))
                    Text("Delete Image")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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