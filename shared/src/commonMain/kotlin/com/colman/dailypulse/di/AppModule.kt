package com.colman.dailypulse.di

import com.colman.dailypulse.data.firebase.FirebaseRepository
import com.colman.dailypulse.data.firebase.RemoteFirebaseRepository
import com.colman.dailypulse.data.habits.HabitsRepository
import com.colman.dailypulse.data.habits.RemoteHabitsRepository
import com.colman.dailypulse.data.posts.PostsRepository
import com.colman.dailypulse.data.posts.RemotePostsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(listOf(commonModule, platformModule))
    }
}

fun initKoin() = initKoin {  }

expect val platformModule: Module

val commonModule = module {
    singleOf(::createJson)
    singleOf(::RemoteFirebaseRepository).bind<FirebaseRepository>()
    singleOf(::RemoteHabitsRepository).bind<HabitsRepository>()
    singleOf(::RemotePostsRepository).bind<PostsRepository>()

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