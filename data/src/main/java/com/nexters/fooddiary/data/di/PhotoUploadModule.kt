package com.nexters.fooddiary.data.di

import com.nexters.fooddiary.data.repository.PhotoRepositoryImpl
import com.nexters.fooddiary.domain.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PhotoUploadModule {

    @Binds
    @Singleton
    internal abstract fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository
}
