package com.nexters.fooddiary.data.di

import com.nexters.fooddiary.data.repository.RestaurantRepositoryImpl
import com.nexters.fooddiary.domain.repository.RestaurantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RestaurantModule {

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        restaurantRepositoryImpl: RestaurantRepositoryImpl,
    ): RestaurantRepository
}
