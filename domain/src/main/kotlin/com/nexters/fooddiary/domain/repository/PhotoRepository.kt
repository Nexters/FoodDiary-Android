package com.nexters.fooddiary.domain.repository

import java.time.LocalDate

interface PhotoRepository {
    suspend fun batchUpload(
        date: LocalDate,
        photoUriStrings: List<String>
    ): Result<Unit>
}
