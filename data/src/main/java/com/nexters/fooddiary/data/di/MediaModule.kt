package com.nexters.fooddiary.data.di

import android.content.ContentResolver
import android.content.Context
import com.nexters.fooddiary.data.repository.ClassificationRepositoryImpl
import com.nexters.fooddiary.data.repository.MediaRepositoryImpl
import com.nexters.fooddiary.domain.repository.ClassificationRepository
import com.nexters.fooddiary.domain.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver = context.contentResolver
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaRepositoryModule {

    @Binds
    @Singleton
    internal abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository
    
    @Binds
    @Singleton
    internal abstract fun bindClassificationRepository(
        classificationRepositoryImpl: ClassificationRepositoryImpl
    ): ClassificationRepository
}
