package com.nexters.fooddiary.data.mapper

import com.nexters.fooddiary.data.remote.restaurant.model.response.RestaurantResponseItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RestaurantMapperTest {

    @Test
    fun `address_name 과 road_address 를 각각 보존해 매핑한다`() {
        val item = RestaurantResponseItem(
            name = "식당",
            addressName = "서울시 강남구 역삼동 123-4",
            roadAddress = "서울시 강남구 테헤란로 123",
            url = "https://example.com",
        )

        val mapped = item.toDomainModel()

        assertEquals("서울시 강남구 역삼동 123-4", mapped.addressName)
        assertEquals("서울시 강남구 테헤란로 123", mapped.roadAddress)
    }

    @Test
    fun `address_name 이 없으면 road_address 를 매핑한다`() {
        val item = RestaurantResponseItem(
            name = "식당",
            addressName = null,
            roadAddress = "서울시 강남구 테헤란로 123",
            url = "https://example.com",
        )

        val mapped = item.toDomainModel()

        assertEquals("서울시 강남구 테헤란로 123", mapped.roadAddress)
    }
}
