package com.colman.dailypulse.data.posts

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.models.posts.Post
import com.colman.dailypulse.models.posts.Posts

interface PostsRepository {
    suspend fun getPosts(): Result<Posts, Error>
    suspend fun createPost(post: Post): Result<String, Error>
    suspend fun likePost(postId: String): Result<String, Error>
    suspend fun deletePost(postId: String): Result<String, Error>
}