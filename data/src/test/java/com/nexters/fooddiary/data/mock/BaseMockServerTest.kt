package com.nexters.fooddiary.data.mock

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before

open class BaseMockServerTest {

    protected lateinit var mockWebServer: MockWebServer

    @Before
    open fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = FoodDiaryMockDispatcher()
        mockWebServer.start()
    }

    @After
    open fun tearDown() {
        mockWebServer.shutdown()
    }
}
