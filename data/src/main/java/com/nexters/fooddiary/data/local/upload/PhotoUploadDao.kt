package com.nexters.fooddiary.data.local.upload

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoUploadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<PhotoUploadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PhotoUploadEntity)

    @Query("SELECT * FROM photo_upload WHERE uploadDate = :date ORDER BY createdAt DESC")
    fun getByUploadDate(date: String): Flow<List<PhotoUploadEntity>>

    @Query("SELECT * FROM photo_upload ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PhotoUploadEntity>>

    @Query("DELETE FROM photo_upload WHERE status = 'PENDING'")
    suspend fun deleteAllPending()
}
