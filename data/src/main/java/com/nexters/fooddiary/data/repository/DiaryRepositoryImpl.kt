package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.DiaryMapper
import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import java.time.YearMonth
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
            testMode = isDebug,
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
            testMode = isDebug,
        )
        return response.diaries
            .groupBy { LocalDate.parse(it.diaryDate) }
            .mapValues { (_, list) -> diaryMapper.toDomainDiaryEntries(list).first() }
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
                ?.let { parsedDate -> parsedDate to summary.photos.map { it.url } }
        }.toMap()
    }

    override suspend fun getDiariesSummary(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, List<String>> {
        // 현재 구현에서는 일/주/월 모두 동일한 summary API를 사용하므로
        // 내부적으로 재사용한다.
        return getDiarySummary(startDate = startDate, endDate = endDate)
    }
}
