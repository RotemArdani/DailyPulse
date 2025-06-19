package com.colman.dailypulse.di

import com.colman.dailypulse.features.habit.HabitViewModel
import com.colman.dailypulse.features.habits.HabitsViewModel
import com.colman.dailypulse.features.posts.PostsViewModel
import org.koin.mp.KoinPlatform

fun habitsViewModel(): HabitsViewModel = KoinPlatform.getKoin().get()

fun habitViewModel(): HabitViewModel = KoinPlatform.getKoin().get()

fun postsViewModel(): PostsViewModel = KoinPlatform.getKoin().get()

fun doInitKoin() = initKoin()