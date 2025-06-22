package com.colman.dailypulse.domian.posts

import com.colman.dailypulse.data.posts.PostsRepository
import com.colman.dailypulse.models.posts.Post

class CreatePost (
    private val postsRepository: PostsRepository
) {
    suspend operator fun invoke(post: Post) = postsRepository.createPost(post)
}