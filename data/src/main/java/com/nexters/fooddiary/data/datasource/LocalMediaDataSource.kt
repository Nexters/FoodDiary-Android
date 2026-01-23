package com.nexters.fooddiary.data.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore.Images.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

/**
 * 로컬 미디어 데이터를 조회하는 DataSource
 */
class LocalMediaDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    /**
     * 전체 앨범의 모든 사진을 날짜별로 그룹화하여 반환
     * 성능 비교를 위한 전체 스캔 함수
     */
    suspend fun getAllPhotos(): Map<LocalDate, List<PhotoData>> =
        withContext(Dispatchers.IO) {
            val photosByDate = mutableMapOf<LocalDate, MutableList<PhotoData>>()
            
            // 쿼리할 컬럼
            val projection = arrayOf(
                Media._ID,
                Media.DISPLAY_NAME,
                Media.DATE_TAKEN
            )
            
            // 최신순 정렬
            val sortOrder = "${Media.DATE_TAKEN} DESC"
            
            try {
                contentResolver.query(
                    Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,  // 조건 없음 - 전체 스캔
                    null,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(Media._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
                    val dateTakenColumn = cursor.getColumnIndexOrThrow(Media.DATE_TAKEN)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val dateTaken = cursor.getLong(dateTakenColumn)
                        
                        val contentUri = ContentUris.withAppendedId(
                            Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        
                        // 밀리초를 LocalDate로 변환
                        val photoDate = Instant.ofEpochMilli(dateTaken)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        
                        photosByDate.getOrPut(photoDate) { mutableListOf() }.add(
                            PhotoData(
                                uri = contentUri,
                                displayName = name,
                                dateTaken = dateTaken
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                //TODO 에러 핸들링
            }
            
            photosByDate
        }

    /**
     * 특정 월의 사진을 날짜별로 그룹화하여 반환
     */
    suspend fun getPhotosByMonth(yearMonth: YearMonth): Map<LocalDate, List<PhotoData>> =
        withContext(Dispatchers.IO) {
            val startDate = yearMonth.atDay(1)
            val endDate = yearMonth.atEndOfMonth()

            val photosByDate = mutableMapOf<LocalDate, MutableList<PhotoData>>()
            
            // LocalDate를 Unix timestamp (밀리초)로 변환
            val startTimestamp = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endTimestamp = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            // 쿼리할 컬럼
            val projection = arrayOf(
                Media._ID,
                Media.DISPLAY_NAME,
                Media.DATE_TAKEN
            )
            
            // 날짜 범위 조건
            val selection = "${Media.DATE_TAKEN} >= ? AND ${Media.DATE_TAKEN} < ?"
            val selectionArgs = arrayOf(startTimestamp.toString(), endTimestamp.toString())
            
            // 최신순 정렬
            val sortOrder = "${Media.DATE_TAKEN} DESC"
            
            try {
                contentResolver.query(
                    Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(Media._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
                    val dateTakenColumn = cursor.getColumnIndexOrThrow(Media.DATE_TAKEN)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val dateTaken = cursor.getLong(dateTakenColumn)
                        
                        val contentUri = ContentUris.withAppendedId(
                            Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        
                        // 밀리초를 LocalDate로 변환
                        val photoDate = Instant.ofEpochMilli(dateTaken)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        
                        photosByDate.getOrPut(photoDate) { mutableListOf() }.add(
                            PhotoData(
                                uri = contentUri,
                                displayName = name,
                                dateTaken = dateTaken
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                //TODO 에러 핸들링
            }
            
            photosByDate
        }

    data class PhotoData(
        val uri: Uri,
        val displayName: String,
        val dateTaken: Long
    )
}
