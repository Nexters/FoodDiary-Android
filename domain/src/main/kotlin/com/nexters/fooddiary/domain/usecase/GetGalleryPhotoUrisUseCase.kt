package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.MediaRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * 이미지 피커에서 갤러리 그리드에 표시할 사진 URI 목록을 반환한다.
 * @param filterDate null이면 갤러리 전체(최신순), 지정 시 해당 날짜 사진만 반환.
 *                   홈/디테일에서는 해당 날짜만, 수정에서는 null로 전체 갤러리.
 */
class GetGalleryPhotoUrisUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(filterDate: LocalDate? = null): List<String> {
        return if (filterDate != null) {
            val yearMonth = YearMonth.from(filterDate)
            val photosByDate = mediaRepository.getPhotosByMonth(yearMonth)
            (photosByDate[filterDate] ?: emptyList())
                .sortedByDescending { it.dateTaken }
                .map { it.uri }
        } else {
            val photosByDate = mediaRepository.getAllPhotos()
            photosByDate
                .values
                .flatten()
                .sortedByDescending { it.dateTaken }
                .map { it.uri }
        }
    }
}
