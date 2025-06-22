package com.colman.dailypulse.features.posts

import com.colman.dailypulse.models.posts.Posts

public sealed class PostsState {
    data object Loading: PostsState()
    data class Loaded(
        val posts: Posts
    ): PostsState()
    data class Error(
        var errorMessage: String
    ): PostsState()
}