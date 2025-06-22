package com.colman.dailypulse.domian.posts

import com.colman.dailypulse.data.posts.PostsRepository

class GetPosts(
    private val postsRepository: PostsRepository
) {
    suspend operator fun invoke() = postsRepository.getPosts()
}