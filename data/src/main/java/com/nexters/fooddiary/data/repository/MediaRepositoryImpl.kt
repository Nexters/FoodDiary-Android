package com.nexters.fooddiary.data.repository

import com.nexters.fooddiary.data.datasource.LocalMediaDataSource
import com.nexters.fooddiary.domain.model.MediaItem
import com.nexters.fooddiary.domain.repository.MediaRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * MediaRepository 구현체
 */
class MediaRepositoryImpl @Inject constructor(
    private val localMediaDataSource: LocalMediaDataSource
) : MediaRepository {

    override suspend fun getPhotosByMonth(yearMonth: YearMonth): Map<LocalDate, List<MediaItem>> {
        val photosByDate = localMediaDataSource.getPhotosByMonth(yearMonth)
        
        return photosByDate.mapValues { (_, photos) ->
            photos.map { photo ->
                MediaItem(
                    uri = photo.uri.toString(),
                    displayName = photo.displayName,
                    dateTaken = photo.dateTaken
                )
            }
        }
    }

    override suspend fun getPhotoCountByDate(yearMonth: YearMonth): Map<LocalDate, Int> {
        // TODO: 성능 최적화 필요
        // 현재는 개수를 세기 위해 모든 MediaItem 객체를 메모리에 로드 중
        // DataSource에 개수 조회 전용 쿼리를 구현하여 최적화 필요
        val photosByDate = getPhotosByMonth(yearMonth)
        return photosByDate.mapValues { it.value.size }
    }

    override suspend fun getAllPhotos(): Map<LocalDate, List<MediaItem>> {
        val photosByDate = localMediaDataSource.getAllPhotos()
        
        return photosByDate.mapValues { (_, photos) ->
            photos.map { photo ->
                MediaItem(
                    uri = photo.uri.toString(),
                    displayName = photo.displayName,
                    dateTaken = photo.dateTaken
                )
            }
        }
    }

    override suspend fun getAllPhotoCountByDate(): Map<LocalDate, Int> {
        val photosByDate = getAllPhotos()
        return photosByDate.mapValues { it.value.size }
    }
}
