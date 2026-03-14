package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.mapper.DiaryMapper
import com.nexters.fooddiary.data.remote.diary.DiaryApi
import com.nexters.fooddiary.data.remote.diary.model.UpdateDiaryRequest
import com.nexters.fooddiary.domain.model.DiaryDetail
import com.nexters.fooddiary.domain.model.DiaryEntry
import com.nexters.fooddiary.domain.model.UpdateDiaryParam
import com.nexters.fooddiary.domain.repository.DiaryRepository
import java.time.LocalDate
import java.time.LocalDateTime
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
            diary.diaryDate.toLocalDateOrNull() == date
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
            .mapNotNull { diary ->
                diary.diaryDate.toLocalDateOrNull()?.let { parsedDate ->
                    parsedDate to diary
                }
            }
            .groupBy({ it.first }, { it.second })
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
        return getDiarySummary(startDate = startDate, endDate = endDate)
    }

    override suspend fun getDiary(id: Int): DiaryEntry {
        val response = diaryApi.getDiaryById(id, testMode = false)
        return diaryMapper.toDomainDiaryEntries(listOf(response)).first()
    }

    override suspend fun updateDiary(diaryId: Int, param: UpdateDiaryParam): DiaryEntry {
        val request = UpdateDiaryRequest(
            category = param.category,
            restaurantName = param.restaurantName,
            restaurantUrl = param.restaurantUrl,
            addressName = param.addressName ?: param.roadAddress,
            roadAddress = param.roadAddress ?: param.addressName,
            tags = param.tags,
            note = param.note,
            coverPhotoId = param.coverPhotoId,
            photoIds = param.photoIds,
        )
        val response = diaryApi.updateDiary(diaryId = diaryId, request = request)
        return diaryMapper.toDomainDiaryEntries(listOf(response)).first()
    }

    override suspend fun deleteDiary(diaryId: Int) {
        diaryApi.deleteDiary(diaryId)
    }
}

private fun String.toLocalDateOrNull(): LocalDate? {
    return runCatching { LocalDate.parse(this) }
        .getOrElse {
            runCatching { LocalDateTime.parse(this).toLocalDate() }.getOrNull()
        }
}
