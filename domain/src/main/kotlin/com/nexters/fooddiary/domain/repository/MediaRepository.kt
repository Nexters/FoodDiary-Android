package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.MediaItem
import java.time.LocalDate
import java.time.YearMonth

interface MediaRepository {
    suspend fun getPhotosByMonth(yearMonth: YearMonth): Map<LocalDate, List<MediaItem>>
    suspend fun getPhotoCountByDate(yearMonth: YearMonth): Map<LocalDate, Int>
    suspend fun getAllPhotos(): Map<LocalDate, List<MediaItem>>
    suspend fun getAllPhotoCountByDate(): Map<LocalDate, Int>
}
