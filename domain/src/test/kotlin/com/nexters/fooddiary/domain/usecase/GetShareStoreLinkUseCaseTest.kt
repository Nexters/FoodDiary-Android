package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.repository.ShareConfigRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetShareStoreLinkUseCaseTest {
    @Test
    fun `returns configured store link when available`() {
        val useCase = GetShareStoreLinkUseCase(
            shareConfigRepository = FakeShareConfigRepository("https://play.google.com/store/apps/details?id=com.example")
        )

        assertEquals(
            "https://play.google.com/store/apps/details?id=com.example",
            useCase()
        )
    }

    @Test
    fun `returns null when store link is unavailable`() {
        val useCase = GetShareStoreLinkUseCase(
            shareConfigRepository = FakeShareConfigRepository(null)
        )

        assertNull(useCase())
    }

    private class FakeShareConfigRepository(
        private val storeLink: String?,
    ) : ShareConfigRepository {
        override fun getShareStoreLink(): String? = storeLink
    }
}
