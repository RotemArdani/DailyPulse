package com.colman.dailypulse.domian.posts

import com.colman.dailypulse.data.posts.PostsRepository

class LikePost (
    private val postsRepository: PostsRepository
) {
    suspend operator fun invoke(postId: String) = postsRepository.likePost(postId)
}