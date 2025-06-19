package com.colman.dailypulse.data.posts

import com.colman.dailypulse.data.Error
import com.colman.dailypulse.data.Result
import com.colman.dailypulse.models.Post
import com.colman.dailypulse.models.Posts

interface PostsRepository {
    suspend fun getPosts(): Result<Posts, Error>
    suspend fun createPost(post: Post): Result<String, Error>
}