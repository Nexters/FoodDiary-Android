package com.nexters.fooddiary.data.di

import com.nexters.fooddiary.data.repository.InsightRepositoryImpl
import com.nexters.fooddiary.domain.repository.InsightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InsightModule {

    @Binds
    @Singleton
    abstract fun bindInsightRepository(
        insightRepositoryImpl: InsightRepositoryImpl,
    ): InsightRepository
}
