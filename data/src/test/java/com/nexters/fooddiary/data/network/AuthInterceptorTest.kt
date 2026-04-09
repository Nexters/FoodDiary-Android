package com.nexters.fooddiary.data.network

import com.nexters.fooddiary.core.common.network.AppErrorEvent
import com.nexters.fooddiary.core.common.network.AppErrorNotifier
import com.nexters.fooddiary.core.common.network.NetworkError
import com.nexters.fooddiary.data.local.TokenStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CancellationException

class AuthInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var tokenStore: TokenStore
    private lateinit var notifier: FakeAppErrorNotifier

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        tokenStore = mockk()
        notifier = FakeAppErrorNotifier()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `HTTP 에러 응답이면 notifier로 Http 에러를 전달한다`() {
        every { tokenStore.getCachedToken() } returns null
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"message":"서버 에러"}""")
        )

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore, notifier))
            .build()

        val response = client.newCall(
            Request.Builder()
                .url(mockWebServer.url("/diaries"))
                .build()
        ).execute()

        assertEquals(500, response.code)
        assertEquals(1, notifier.captured.size)
        val event = notifier.captured.single()
        val error = event.error as NetworkError.Http
        assertEquals(500, error.code)
        assertEquals("서버 에러", error.message)
        assertEquals("/diaries", event.path)
    }

    @Test
    fun `네트워크 IOException이면 notifier로 NoConnection 에러를 전달한다`() {
        every { tokenStore.getCachedToken() } returns null
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        )

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore, notifier))
            .build()

        try {
            client.newCall(
                Request.Builder()
                    .url(mockWebServer.url("/insights"))
                    .build()
            ).execute()
            fail("IOException expected")
        } catch (_: IOException) {
            // expected
        }

        assertEquals(1, notifier.captured.size)
        val event = notifier.captured.single()
        assertTrue(event.error is NetworkError.NoConnection)
        assertEquals("/insights", event.path)
    }

    @Test
    fun `에러 응답이 plain text면 fallback 메시지로 body를 전달한다`() {
        every { tokenStore.getCachedToken() } returns null
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "text/plain")
                .setBody("internal server error")
        )

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore, notifier))
            .build()

        val response = client.newCall(
            Request.Builder()
                .url(mockWebServer.url("/plain-error"))
                .build()
        ).execute()

        assertEquals(500, response.code)
        val error = notifier.captured.single().error as NetworkError.Http
        assertEquals("internal server error", error.message)
    }

    @Test
    fun `CancellationException이면 notifier에 전달하지 않고 그대로 던진다`() {
        every { tokenStore.getCachedToken() } returns null
        val interceptor = AuthInterceptor(tokenStore, notifier)
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder()
            .url("https://example.com/test")
            .build()
        every { chain.request() } returns request
        every { chain.proceed(any()) } throws CancellationException("cancelled")

        try {
            interceptor.intercept(chain)
            fail("CancellationException expected")
        } catch (_: CancellationException) {
            // expected
        }

        assertTrue(notifier.captured.isEmpty())
    }
}

private class FakeAppErrorNotifier : AppErrorNotifier {
    private val _events = MutableSharedFlow<AppErrorEvent>()
    override val events: SharedFlow<AppErrorEvent> = _events
    val captured = mutableListOf<AppErrorEvent>()

    override fun notify(event: AppErrorEvent) {
        captured += event
    }
}
