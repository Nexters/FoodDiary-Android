package com.nexters.fooddiary.data.local.upload

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoUploadEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FoodDiaryDatabase : RoomDatabase() {
    abstract fun photoUploadDao(): PhotoUploadDao
}
