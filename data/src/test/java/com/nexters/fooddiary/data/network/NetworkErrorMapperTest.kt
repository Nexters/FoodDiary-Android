package com.nexters.fooddiary.data.network

import com.nexters.fooddiary.core.common.network.NetworkError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkErrorMapperTest {

    @Test
    fun `HttpException은 Http 에러로 매핑된다`() {
        val exception = HttpException(
            Response.error<Unit>(
                503,
                """{"message":"server down"}""".toResponseBody("application/json".toMediaType()),
            )
        )

        val mapped = exception.toNetworkError()

        assertTrue(mapped is NetworkError.Http)
        val http = mapped as NetworkError.Http
        assertEquals(503, http.code)
    }

    @Test
    fun `SocketTimeoutException은 Timeout으로 매핑된다`() {
        val mapped = SocketTimeoutException("timeout").toNetworkError()

        assertTrue(mapped is NetworkError.Timeout)
    }

    @Test
    fun `UnknownHostException은 NoConnection으로 매핑된다`() {
        val mapped = UnknownHostException("no host").toNetworkError()

        assertTrue(mapped is NetworkError.NoConnection)
    }

    @Test
    fun `IOException은 NoConnection으로 매핑된다`() {
        val mapped = IOException("io").toNetworkError()

        assertTrue(mapped is NetworkError.NoConnection)
    }

    @Test
    fun `기타 예외는 Unknown으로 매핑된다`() {
        val mapped = IllegalStateException("boom").toNetworkError()

        assertTrue(mapped is NetworkError.Unknown)
        val unknown = mapped as NetworkError.Unknown
        assertEquals("boom", unknown.message)
    }
}
