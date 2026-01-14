package com.nexters.fooddiary.data.di

import org.koin.dsl.module

val dataModule = module {
    // Add repositories here
    // single<FoodRepository> { FoodRepositoryImpl(get(), get()) }

    // Add data sources here
    // single { FoodRemoteDataSource(get()) }
    // single { FoodLocalDataSource(get()) }
}
