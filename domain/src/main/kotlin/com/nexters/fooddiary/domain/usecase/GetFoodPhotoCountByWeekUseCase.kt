package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.ClassificationRepository
import com.nexters.fooddiary.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class GetFoodPhotoCountByWeekUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val classificationRepository: ClassificationRepository
) {
    /** 미디어 조회는 IO, 분류(CPU)는 Default에서 실행해 메인 스레드를 막지 않음 */
    suspend operator fun invoke(): Int = coroutineScope {
        val (startOfWeek, endOfWeek) = currentWeekRange()
        val photosByDate = withContext(Dispatchers.IO) {
            mediaRepository.getPhotosBetween(startOfWeek, endOfWeek)
        }
        if (photosByDate.isEmpty()) return@coroutineScope 0

        photosByDate
            .map { (_, mediaItems) ->
                async(Dispatchers.Default) {
                    mediaItems
                        .map { item ->
                            async { classificationRepository.classifyImage(item.uri) }
                        }
                        .awaitAll()
                        .count { it?.isFood == true }
                }
            }
            .awaitAll()
            .sum()
    }

    private fun currentWeekRange(): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        val firstDayOfWeek = DayOfWeek.SUNDAY
        val daysFromStart = (today.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
        val startOfWeek = today.minusDays(daysFromStart.toLong())
        val endOfWeek = startOfWeek.plusDays(6)
        return startOfWeek to endOfWeek
    }
}
