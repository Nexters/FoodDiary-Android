package com.nexters.fooddiary.core.network.di

import com.nexters.fooddiary.core.network.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    // Json
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    // OkHttpClient
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addDebugInterceptors()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://api.fooddiary.com/") //FIXME API URL
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // Add API service instances here when needed
    // single { get<Retrofit>().create(FoodDiaryApi::class.java) }
}

fun OkHttpClient.Builder.addDebugInterceptors(): OkHttpClient.Builder {
    if (BuildConfig.DEBUG) {
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }
    return this
}
