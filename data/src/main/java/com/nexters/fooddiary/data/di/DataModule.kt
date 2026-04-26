package com.nexters.fooddiary.data.di

import com.google.firebase.auth.FirebaseAuth
import com.nexters.fooddiary.data.auth.GoogleSignInHelper
import com.nexters.fooddiary.data.firebase.AndroidLoginDeviceInfoProvider
import com.nexters.fooddiary.data.firebase.LoginDeviceInfoProvider
import com.nexters.fooddiary.data.firebase.LoginFirebaseTokenProvider
import com.nexters.fooddiary.data.firebase.LoginFirebaseTokenProviderImpl
import com.nexters.fooddiary.data.repository.DiaryRepositoryImpl
import com.nexters.fooddiary.data.repository.ReviewPromptRepositoryImpl
import com.nexters.fooddiary.data.repository.AuthRepositoryImpl
import com.nexters.fooddiary.data.repository.UserRepositoryImpl
import com.nexters.fooddiary.core.common.auth.GoogleSignInIntentProvider
import com.nexters.fooddiary.core.common.resource.ResourceProvider
import com.nexters.fooddiary.core.common.resource.AndroidResourceProvider
import com.nexters.fooddiary.domain.repository.AuthRepository
import com.nexters.fooddiary.domain.repository.DiaryRepository
import com.nexters.fooddiary.domain.repository.ReviewPromptRepository
import com.nexters.fooddiary.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(
        diaryRepositoryImpl: DiaryRepositoryImpl
    ): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindReviewPromptRepository(
        reviewPromptRepositoryImpl: ReviewPromptRepositoryImpl
    ): ReviewPromptRepository

    @Binds
    @Singleton
    abstract fun bindGoogleSignInIntentProvider(
        googleSignInHelper: GoogleSignInHelper
    ): GoogleSignInIntentProvider

    @Binds
    @Singleton
    abstract fun bindLoginFirebaseTokenProvider(
        loginFirebaseTokenProviderImpl: LoginFirebaseTokenProviderImpl
    ): LoginFirebaseTokenProvider

    @Binds
    @Singleton
    abstract fun bindLoginDeviceInfoProvider(
        androidLoginDeviceInfoProvider: AndroidLoginDeviceInfoProvider
    ): LoginDeviceInfoProvider

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        androidResourceProvider: AndroidResourceProvider
    ): ResourceProvider

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    }
}
