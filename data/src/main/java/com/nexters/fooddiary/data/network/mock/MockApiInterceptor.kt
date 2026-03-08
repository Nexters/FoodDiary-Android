package com.nexters.fooddiary.data.network.mock

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

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
            Log.i(TAG, "Mock Response: $method $path -> $mockFileName")
            createMockResponse(chain, mockFileName, path)
        } else {
            Log.i(TAG, "Pass-through: $method $path (no mock)")
            chain.proceed(request)
        }
    }

    private fun createMockResponse(chain: Interceptor.Chain, fileName: String, path: String): Response {
        var jsonBody = readAssetFile(fileName)
        // GET /diaries/{date} mock: 응답 JSON의 date를 요청한 날짜로 치환
        if (fileName == "get_diary_20260117.json" && path.matches(Regex("/diaries/\\d{4}-\\d{2}-\\d{2}"))) {
            val requestedDate = path.removePrefix("/diaries/")
            jsonBody = jsonBody.replace("\"date\":\"2026-01-17\"", "\"date\":\"$requestedDate\"")
        }
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
