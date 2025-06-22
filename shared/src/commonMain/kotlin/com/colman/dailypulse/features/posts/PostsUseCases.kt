package com.colman.dailypulse.features.posts

import com.colman.dailypulse.domian.posts.CreatePost
import com.colman.dailypulse.domian.posts.GetPosts

class PostsUseCases (
    val getPosts: GetPosts,
    val createPost: CreatePost,
)