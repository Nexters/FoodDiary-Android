package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.PhotoRepository
import java.time.LocalDate
import javax.inject.Inject

class BatchUploadPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(
        date: LocalDate,
        photoUriStrings: List<String>
    ): Result<Unit> {
        return photoRepository.batchUpload(date, photoUriStrings)
    }
}
