package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.ClassificationRepository
import com.nexters.fooddiary.domain.repository.MediaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class GetFoodPhotoCountByWeekUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val classificationRepository: ClassificationRepository
) {
    suspend operator fun invoke(): Int = coroutineScope {
        val (startOfWeek, endOfWeek) = currentWeekRange()
        val photosByDate = mediaRepository.getPhotosBetween(startOfWeek, endOfWeek)
        if (photosByDate.isEmpty()) return@coroutineScope 0

        photosByDate
            .map { (_, mediaItems) ->
                async {
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
