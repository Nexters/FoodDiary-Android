package com.nexters.fooddiary.domain.repository

import com.nexters.fooddiary.domain.model.DiaryDetail
import java.time.LocalDate

interface DiaryRepository {
    suspend fun getDiary(date: LocalDate): DiaryDetail
}
