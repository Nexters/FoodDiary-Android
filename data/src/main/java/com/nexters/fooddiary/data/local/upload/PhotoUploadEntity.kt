package com.nexters.fooddiary.data.local.upload

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_upload")
data class PhotoUploadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** 서버 photo_id (PENDING일 때만 존재) */
    val photoId: Long? = null,
    /** 서버 diary_id (PENDING일 때만 존재) */
    val diaryId: Long? = null,
    /** 서버 image_url (PENDING일 때만 존재) */
    val imageUrl: String? = null,
    /** time_type (PENDING일 때만 존재) */
    val timeType: String? = null,
    /** 업로드 시도한 날짜 (ISO_LOCAL_DATE) */
    val uploadDate: String,
    val status: UploadStatus,
    /** 실패 시 에러 메시지 (FAILURE일 때만) */
    val errorMessage: String? = null,
    /** 레코드 생성 시각 (epoch millis) */
    val createdAt: Long = System.currentTimeMillis()
)
