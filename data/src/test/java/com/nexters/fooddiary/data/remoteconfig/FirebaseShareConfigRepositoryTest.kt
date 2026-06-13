package com.nexters.fooddiary.data.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FirebaseShareConfigRepositoryTest {
    private val remoteConfig = mockk<FirebaseRemoteConfig>()

    @Test
    fun `returns null when share store link is disabled`() {
        val repository = FirebaseShareConfigRepository(
            remoteConfig = remoteConfig,
            shareBaseUrl = "https://mumuk.ai.kr/",
        )
        every { remoteConfig.getBoolean("use_store_link") } returns false

        assertNull(repository.getShareStoreLink())
    }

    @Test
    fun `returns null when configured base url is blank`() {
        val repository = FirebaseShareConfigRepository(
            remoteConfig = remoteConfig,
            shareBaseUrl = "   ",
        )
        every { remoteConfig.getBoolean("use_store_link") } returns true

        assertNull(repository.getShareStoreLink())
    }

    @Test
    fun `returns trimmed base url when feature is enabled`() {
        val repository = FirebaseShareConfigRepository(
            remoteConfig = remoteConfig,
            shareBaseUrl = " https://mumuk.ai.kr/ ",
        )
        every { remoteConfig.getBoolean("use_store_link") } returns true

        assertEquals(
            "https://mumuk.ai.kr/",
            repository.getShareStoreLink()
        )
    }
}
