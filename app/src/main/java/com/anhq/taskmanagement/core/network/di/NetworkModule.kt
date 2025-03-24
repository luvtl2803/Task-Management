package com.anhq.taskmanagement.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

const val LOCAL_BASE_URL = "https://naubvopylgzvanprvkny.supabase.co/storage/v1/object/public/task-storage//"
const val REQUEST_TIMEOUT = 60000L

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesLocalHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            defaultRequest { url(LOCAL_BASE_URL) }
            install(Logging) {
                logger = Logger.ANDROID
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = REQUEST_TIMEOUT
            }
        }
    }
}