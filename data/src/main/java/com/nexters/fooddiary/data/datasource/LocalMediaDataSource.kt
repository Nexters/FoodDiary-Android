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
            queryPhotos()
        }

    /**
     * 특정 월의 사진을 날짜별로 그룹화하여 반환
     */
    suspend fun getPhotosByMonth(yearMonth: YearMonth): Map<LocalDate, List<PhotoData>> =
        withContext(Dispatchers.IO) {
            val startDate = yearMonth.atDay(1)
            val endDate = yearMonth.atEndOfMonth()

            // LocalDate를 Unix timestamp (초)로 변환
            val startTimestamp = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            val endTimestamp = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

            // 날짜 범위 조건
            val selection = "${Media.DATE_MODIFIED} >= ? AND ${Media.DATE_MODIFIED} < ?"
            val selectionArgs = arrayOf(
                startTimestamp.toString(),
                endTimestamp.toString()
            )

            queryPhotos(selection, selectionArgs)
        }

    private fun queryPhotos(
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): Map<LocalDate, List<PhotoData>> {
        val photosByDate = mutableMapOf<LocalDate, MutableList<PhotoData>>()

        // 쿼리할 컬럼
        val projection = arrayOf(
            Media._ID,
            Media.DATE_TAKEN,
            Media.DATE_MODIFIED
        )

        // 최신순 정렬
        val sortOrder = "${Media.DATE_MODIFIED} DESC"

        try {
            contentResolver.query(
                Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(Media._ID)
                val dateTakenColumn = cursor.getColumnIndexOrThrow(Media.DATE_TAKEN)
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(Media.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val dateTaken = cursor.getLong(dateTakenColumn)
                    val dateModified = cursor.getLong(dateModifiedColumn)

                    // DATE_TAKEN이 있으면(0보다 크면) 사용하고, 없으면 DATE_MODIFIED 사용
                    // DATE_TAKEN은 ms 단위이므로 초 단위로 변환 필요
                    val timestamp = if (dateTaken > 0) {
                        dateTaken / 1000
                    } else {
                        dateModified
                    }

                    val photo = PhotoData(
                        uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id),
                        dateTaken = timestamp
                    )

                    val photoDate = photo.dateTaken.toLocalDate()
                    photosByDate.getOrPut(photoDate) { mutableListOf() }.add(photo)
                }
            }
        } catch (e: Exception) {
            //TODO 에러 핸들링
        }
        return photosByDate
    }

    data class PhotoData(
        val uri: Uri,
        val dateTaken: Long  // DATE_MODIFIED단위가 초 단위 (epoch time)
    )
}

private fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}
