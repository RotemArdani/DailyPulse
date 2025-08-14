package com.colman.dailypulse.features.posts

import com.colman.dailypulse.domian.posts.CreatePost
import com.colman.dailypulse.domian.posts.DeletePost
import com.colman.dailypulse.domian.posts.GetPosts
import com.colman.dailypulse.domian.posts.LikePost

class PostsUseCases (
    val getPosts: GetPosts,
    val createPost: CreatePost,
    val likePost: LikePost,
    val deletePost: DeletePost
)