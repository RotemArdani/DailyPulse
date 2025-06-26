package com.colman.dailypulse.di

import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.firebase.RemoteFirebaseRepository
import com.colman.dailypulse.data.habits.HabitsRepository
import com.colman.dailypulse.data.habits.RemoteHabitsRepository
import com.colman.dailypulse.data.posts.PostsRepository
import com.colman.dailypulse.data.posts.RemotePostsRepository
import com.colman.dailypulse.data.user.RemoteUserRepository
import com.colman.dailypulse.data.user.UserRepository
import com.colman.dailypulse.domian.habits.CreateHabit
import com.colman.dailypulse.domian.habits.GetHabits
import com.colman.dailypulse.domian.habits.OnHabitDone
import com.colman.dailypulse.domian.habits.UpdateHabit
import com.colman.dailypulse.domian.posts.CreatePost
import com.colman.dailypulse.domian.posts.GetPosts
import com.colman.dailypulse.domian.posts.LikePost
import com.colman.dailypulse.domian.user.OnSignIn
import com.colman.dailypulse.domian.user.OnSignUp
import com.colman.dailypulse.features.habits.HabitsUseCases
import com.colman.dailypulse.features.posts.PostsUseCases
import com.colman.dailypulse.features.user.UserUseCases
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(listOf(commonModule, platformModule, habitsDomainModule, postsDomainModule, userDomainModule))
    }
}

fun initKoin() = initKoin {  }

expect val platformModule: Module

val habitsDomainModule = module {
    factoryOf(::GetHabits)
    factoryOf(::CreateHabit)
    factoryOf(::UpdateHabit)
    factoryOf(::OnHabitDone)
    factoryOf(::HabitsUseCases)
}

val postsDomainModule = module {
    factoryOf(::GetPosts)
    factoryOf(::CreatePost)
    factoryOf(::LikePost)
    factoryOf(::PostsUseCases)
}

val userDomainModule = module {
    factoryOf(::OnSignIn)
    factoryOf(::OnSignUp)
    factoryOf(::UserUseCases)
}

val commonModule = module {
    singleOf(::createJson)
    singleOf(::RemoteFirebaseRepository).bind<FirebaseRepository>()
    singleOf(::RemoteHabitsRepository).bind<HabitsRepository>()
    singleOf(::RemotePostsRepository).bind<PostsRepository>()
    singleOf(::RemoteUserRepository).bind<UserRepository>()

    single { createHttpClient(
        clientEngine = get(),
        json = get()
    ) }


}

fun createJson(): Json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
}

fun createHttpClient(clientEngine: HttpClientEngine, json: Json) = HttpClient(clientEngine) {
    install(ContentNegotiation) { json(
        json
    )}
}