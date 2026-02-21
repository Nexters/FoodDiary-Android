package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.DiaryMapper
import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import java.time.YearMonth
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

    override suspend fun getDiaryByMonth(yearMonth: YearMonth): Map<LocalDate, DiaryEntry> {
        val startDate = yearMonth.atDay(1).toString()
        val endDate = yearMonth.atEndOfMonth().toString()
        val response = diaryApi.getDiary(
            startDate = startDate,
            endDate = endDate,
        )
        return response.diaries
            .groupBy { LocalDate.parse(it.diaryDate) }
            .mapValues { (_, list) -> diaryMapper.toDomainDiaryEntries(list).first() }
    }
}
