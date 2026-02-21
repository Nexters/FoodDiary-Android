package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.DiaryMapper
import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryApi: DiaryApi,
    private val diaryMapper: DiaryMapper,
) : DiaryRepository {

    override suspend fun getDiary(date: LocalDate): DiaryDetail {
        val requestedDate = date.toString()
        val response = diaryApi.getDiary(
            startDate = requestedDate,
            endDate = requestedDate,
        )
        val diaries = response.diaries.filter { diary ->
            diary.diaryDate == requestedDate
        }

        return DiaryDetail(
            date = date,
            diaries = diaryMapper.toDomainDiaryEntries(diaries),
        )
    }
}
