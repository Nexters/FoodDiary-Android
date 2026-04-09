package com.nexters.fooddiary.core.common.network

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkNotifierModule {
    @Binds
    @Singleton
    abstract fun bindAppErrorNotifier(
        impl: DefaultAppErrorNotifier,
    ): AppErrorNotifier
}
