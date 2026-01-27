package com.nexters.fooddiary.data.network.mock

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Runtime Mock API Interceptor
 * assets에서 mock JSON을 읽어 응답으로 반환
 */
class MockApiInterceptor(
    private val context: Context
) : Interceptor {

    companion object {
        private const val TAG = "MockApiInterceptor"
        private const val ASSETS_PATH = "api-response"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val mockFileName = MockRouteHandler.getMockFileName(path, method)

        return if (mockFileName != null) {
            Log.d(TAG, "Mock Response: $method $path -> $mockFileName")
            createMockResponse(chain, mockFileName)
        } else {
            Log.d(TAG, "Pass-through: $method $path (no mock)")
            chain.proceed(request)
        }
    }

    private fun createMockResponse(chain: Interceptor.Chain, fileName: String): Response {
        val jsonBody = readAssetFile(fileName)

        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(jsonBody.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun readAssetFile(fileName: String): String {
        return context.assets.open("$ASSETS_PATH/$fileName")
            .bufferedReader()
            .use { it.readText() }
    }
}
