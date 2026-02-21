package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.DiaryMapper
import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named

class DiaryRepositoryImpl @Inject constructor(
    private val diaryApi: DiaryApi,
    private val diaryMapper: DiaryMapper,
    @Named("isDebug") private val isDebug: Boolean,
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

    override suspend fun getDiarySummary(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, List<String>> {
        val response = diaryApi.getDiarySummary(
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            testMode = isDebug,
        )

        return response.mapNotNull { (date, summary) ->
            runCatching { LocalDate.parse(date) }
                .getOrNull()
                ?.let { parsedDate -> parsedDate to summary.photos }
        }.toMap()
    }
}
