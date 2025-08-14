package com.colman.dailypulse.di

import com.colman.dailypulse.features.habit.HabitViewModel
import com.colman.dailypulse.features.habits.HabitsViewModel
import com.colman.dailypulse.features.posts.PostsViewModel
import com.colman.dailypulse.features.user.UserViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create()}

    factoryOf(::HabitsViewModel)
    factory { (habitId: String?) ->
        HabitViewModel(get(), habitId)
    }
    factoryOf(::PostsViewModel)
    factoryOf(::UserViewModel)
}