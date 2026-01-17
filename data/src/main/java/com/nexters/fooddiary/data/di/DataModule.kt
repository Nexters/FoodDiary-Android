package com.nexters.fooddiary.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // Add repository bindings here
    // @Binds
    // @Singleton
    // abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository
}
