package com.colman.dailypulse.domian.posts

import com.colman.dailypulse.data.posts.PostsRepository

class DeletePost (
    private val postsRepository: PostsRepository
    ) {
        suspend operator fun invoke(postId: String) = postsRepository.deletePost(postId)
    }