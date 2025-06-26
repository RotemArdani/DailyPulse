package com.colman.dailypulse.features.posts

import co.touchlab.kermit.Logger
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.features.BaseViewModel
import com.colman.dailypulse.features.habits.SaveState
import com.colman.dailypulse.models.posts.Post
import com.colman.dailypulse.models.posts.Posts
import com.colman.dailypulse.utils.ImageUploader
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsViewModel(
    private val useCases: PostsUseCases,
): BaseViewModel() {
    private val _uiState: MutableStateFlow<PostsState> =
        MutableStateFlow(PostsState.Loading)
    val uiState: StateFlow<PostsState> get() = _uiState

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage

    private val imageUploader = ImageUploader()

    fun onDescriptionChanged(newValue: String) {
        _description.value = newValue
    }

    private var log = Logger.withTag("PostsViewModel")

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        scope.launch {
            val result = useCases.getPosts()

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

    fun uploadImage(imageBytes: ByteArray) {
        scope.launch {
            try {
                val imageUrl = imageUploader.uploadImage(imageBytes)
                log.e("Uploaded image URL: $imageUrl")
                _imageUrl.value = imageUrl
            } catch (e: Exception) {
                log.e("Error uploading image: ${e.message}")
            }
        }
    }

    fun onCreatePost() {
        scope.launch {
            _saveState.value = SaveState.Saving

            when (val result = useCases.createPost(Post(imageUrl = imageUrl.value, description = description.value))) {
                is Result.Success -> {
                    _saveState.value = SaveState.Success
                    fetchPosts()
                }
                is Result.Failure -> {
                    _saveState.value = SaveState.Error(result.error?.message ?: "Unknown error")
                }
            }
        }
    }

    fun toggleLike(postId: String, userId: String) {
        scope.launch {
            try {
                val current = _uiState.value
                if (current !is PostsState.Loaded) return@launch

                val updatedItems = current.posts.items.map { post ->
                    if (post.id == postId) {
                        val newLikes = if (userId in post.likedByUserIds) {
                            post.likedByUserIds - userId
                        } else {
                            post.likedByUserIds + userId
                        }

                        val result = useCases.likePost(post.id?: "")
                        if (result is Result.Failure) {
                            throw Exception("Failed to like post.")
                        }

                        post.copy(likedByUserIds = newLikes)
                    } else {
                        post
                    }
                }
                _uiState.value = PostsState.Loaded(Posts(updatedItems))
            } catch (e: Exception) {
                _snackbarMessage.emit("Failed to like post.")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
        _description.value = ""
        _imageUrl.value = null
    }
}