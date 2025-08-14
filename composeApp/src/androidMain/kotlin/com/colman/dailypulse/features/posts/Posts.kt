package com.colman.dailypulse.features.posts

import PostCard
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.colman.dailypulse.features.habits.ErrorContent
import com.colman.dailypulse.features.habits.LoadingContent
import com.colman.dailypulse.models.posts.Post
import com.colman.dailypulse.models.posts.Posts
import com.colman.dailypulse.utils.LocalSnackbarController
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PostsScreen(
    viewModel: PostsViewModel = koinViewModel(),
    onCreatePostClick: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val userId = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val snackbarController = LocalSnackbarController.current

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { msg ->
            snackbarController.showMessage(msg)
        }
    }

        when (uiState) {
        is PostsState.Error -> ErrorContent(message = uiState.errorMessage)
        is PostsState.Loaded -> PostsContent(
            uiState.posts,
            userId,
            onCreatePostClick,
            onLikePostClick = { post -> viewModel.toggleLike(post.id?: "", userId?: "") },
            onDeletePostClick = { postId -> viewModel.onDeletePost(postId) }
        )

        PostsState.Loading -> LoadingContent()
    }
}

@Composable
fun PostsContent(
    posts: Posts,
    userId: String? = "",
    onCreatePostClick: () -> Unit,
    onLikePostClick: (Post) -> Unit,
    onDeletePostClick: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posts.items) { post ->
                PostCard(
                    post = post,
                    isLiked = post.likedByUserIds.contains(userId),
                    currentUserId = userId?: "",
                    onLikeClick = {onLikePostClick(post)},
                    onDeleteClick = { onDeletePostClick(post.id?: "")}
                )
            }
        }

        FloatingActionButton(
            onClick = onCreatePostClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Post")
        }
    }
}