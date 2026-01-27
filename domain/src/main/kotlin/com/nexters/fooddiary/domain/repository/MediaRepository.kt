package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.MediaItem
import java.time.LocalDate
import java.time.YearMonth

/**
 * 미디어 데이터 Repository 인터페이스
 */
interface MediaRepository {
    /**
     * 특정 월의 사진을 날짜별로 그룹화하여 반환
     */
    suspend fun getPhotosByMonth(yearMonth: YearMonth): Map<LocalDate, List<MediaItem>>
    
    /**
     * 특정 월의 날짜별 사진 개수를 반환
     */
    suspend fun getPhotoCountByDate(yearMonth: YearMonth): Map<LocalDate, Int>
    
    /**
     * 전체 앨범의 모든 사진을 날짜별로 그룹화하여 반환
     * 성능 비교를 위한 전체 스캔 함수
     */
    suspend fun getAllPhotos(): Map<LocalDate, List<MediaItem>>
    
    /**
     * 전체 앨범의 날짜별 사진 개수를 반환
     * 성능 비교를 위한 전체 스캔 함수
     */
    suspend fun getAllPhotoCountByDate(): Map<LocalDate, Int>
}
