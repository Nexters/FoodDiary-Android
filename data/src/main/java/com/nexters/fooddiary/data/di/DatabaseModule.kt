package com.nexters.fooddiary.data.di

import android.content.Context
import androidx.room.Room
import com.nexters.fooddiary.data.local.upload.FoodDiaryDatabase
import com.nexters.fooddiary.data.local.upload.PhotoUploadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "food_diary_db"

    @Provides
    @Singleton
    fun provideFoodDiaryDatabase(
        @ApplicationContext context: Context
    ): FoodDiaryDatabase = Room.databaseBuilder(
        context,
        FoodDiaryDatabase::class.java,
        DB_NAME
    ).build()

    @Provides
    @Singleton
    fun providePhotoUploadDao(database: FoodDiaryDatabase): PhotoUploadDao =
        database.photoUploadDao()
}
