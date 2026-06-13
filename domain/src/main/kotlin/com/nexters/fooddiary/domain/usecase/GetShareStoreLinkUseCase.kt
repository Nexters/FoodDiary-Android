package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.ShareConfigRepository
import javax.inject.Inject

class GetShareStoreLinkUseCase @Inject constructor(
    private val shareConfigRepository: ShareConfigRepository,
) {
    operator fun invoke(): String? = shareConfigRepository.getShareStoreLink()
}
