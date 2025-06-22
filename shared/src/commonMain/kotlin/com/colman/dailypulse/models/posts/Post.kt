package com.colman.dailypulse.models.posts

import com.colman.dailypulse.models.habits.Habit
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String? = "",

    val createdByUserId: String, // ID of the user who created the post

    val habitSnapshot: Habit?,

    val description: String,

    val createdAt: Instant = Clock.System.now(),

    val likedByUserIds: List<String> = emptyList(),

    val imageUrl: String? = null
)