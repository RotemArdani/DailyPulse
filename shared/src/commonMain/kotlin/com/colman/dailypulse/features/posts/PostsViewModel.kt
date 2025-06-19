package com.colman.dailypulse.features.posts

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.posts.PostsRepository
import com.colman.dailypulse.features.BaseViewModel
import com.colman.dailypulse.features.habits.SaveState
import com.colman.dailypulse.models.Post
import com.colman.dailypulse.models.Posts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsViewModel(
    private val respository: PostsRepository,
    private val firebaseRepository: FirebaseRepository
): BaseViewModel() {
    private val _uiState: MutableStateFlow<PostsState> =
        MutableStateFlow(PostsState.Loading)
    val uiState: StateFlow<PostsState> get() = _uiState

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private var log = Logger.withTag("PostsViewModel")

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        scope.launch {
            val result = respository.getPosts()

            when (result) {
                is Result.Success -> {
                    _uiState.emit(
                        PostsState.Loaded( result.data ?: Posts(items = emptyList()))
                    )
                }

                is Result.Failure -> {
                    _uiState.emit(
                        PostsState.Error(errorMessage = result.error?.message ?: "N/A")
                    )
                }
            }
        }
    }

    private fun onCreatePost(post: Post) {
        scope.launch {
            _saveState.value = SaveState.Saving // Indicate saving is in progress

            // Call the repository's saveHabit function
            when (val result = respository.createPost(post)) {
                is Result.Success -> {
                    _saveState.value = SaveState.Success
                    // Optionally, reload habits or update the UI to reflect the change
                    fetchPosts() // Or more sophisticated UI update
                }
                is Result.Failure -> {
                    _saveState.value = SaveState.Error(result.error?.message ?: "Unknown error")
                }
            }
        }
    }
}