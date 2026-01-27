package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.MediaRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * 특정 월의 사진 개수를 조회하는 UseCase
 */
class GetPhotosByMonthUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(yearMonth: YearMonth): Map<LocalDate, Int> {
        return mediaRepository.getPhotoCountByDate(yearMonth)
    }
}
