package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.MediaRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * 전체 앨범의 사진 개수를 조회하는 UseCase
 * 성능 비교를 위한 전체 스캔 UseCase
 */
class GetAllPhotosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(): Map<LocalDate, Int> {
        return mediaRepository.getAllPhotoCountByDate()
    }
}
