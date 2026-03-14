package com.nexters.fooddiary.presentation.modify

import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModifyViewModelStateTest {

    @Test
    fun `toUpdateDiaryParam 은 addressLines 두번째 줄을 addressName 으로 매핑한다`() {
        val state = ModifyState(
            selectedCategory = "한식",
            addressLines = persistentListOf("서울 강남구 테헤란로 123", "서울 강남구 역삼동 123-4"),
            restaurantName = "맛집",
            restaurantUrl = "https://example.com",
            tags = persistentListOf("점심"),
            note = "메모",
            photoIds = persistentListOf(1, 2),
            coverPhotoId = 1,
        )

        val param = state.toUpdateDiaryParam()

        assertEquals("서울 강남구 역삼동 123-4", param.addressName)
        assertEquals("한식", param.category)
        assertEquals("맛집", param.restaurantName)
        assertTrue(param.photoIds?.isNotEmpty() == true)
    }

    @Test
    fun `toUpdateDiaryParam 은 두번째 줄이 없으면 addressName 을 null 로 둔다`() {
        val state = ModifyState(
            addressLines = persistentListOf("서울 강남구 테헤란로 123"),
        )

        val param = state.toUpdateDiaryParam()

        assertNull(param.addressName)
    }

    @Test
    fun `normalizeTag 는 공백만 있는 태그를 null 로 반환한다`() {
        assertNull(normalizeTag("   "))
    }

    @Test
    fun `appendTagIfMissing 는 중복 태그면 null 을 반환한다`() {
        val tags = persistentListOf("점심")
        val result = appendTagIfMissing(tags, "점심")
        assertNull(result)
    }

    @Test
    fun `removePhotoAtState 는 대표사진 삭제시 다음 사진을 대표로 변경한다`() {
        val result = removePhotoAtState(
            photoIds = persistentListOf(10, 20, 30),
            photoUrls = persistentListOf("a", "b", "c"),
            coverPhotoId = 10,
            index = 0,
        )

        assertEquals(listOf(20, 30), result.photoIds)
        assertEquals(listOf("b", "c"), result.photoUrls)
        assertEquals(20, result.coverPhotoId)
    }
}
