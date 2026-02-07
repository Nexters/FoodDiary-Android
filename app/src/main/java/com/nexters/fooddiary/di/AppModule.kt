package com.nexters.fooddiary.di

import com.nexters.fooddiary.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Named("isDebug")
    fun provideIsDebug(): Boolean = BuildConfig.DEBUG

    @Provides
    @Named("useMockApi")
    fun provideUseMockApi(): Boolean = BuildConfig.USE_MOCK_API
}
