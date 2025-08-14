package com.colman.dailypulse.di

import com.colman.dailypulse.features.habit.HabitViewModel
import com.colman.dailypulse.features.habits.HabitsViewModel
import com.colman.dailypulse.features.posts.PostsViewModel
import com.colman.dailypulse.features.user.UserViewModel
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create()}
    
    viewModelOf(::HabitsViewModel)
    viewModel { (habitId: String?) -> HabitViewModel(get(), habitId) }
    viewModelOf(::PostsViewModel)
    viewModelOf(::UserViewModel)
}