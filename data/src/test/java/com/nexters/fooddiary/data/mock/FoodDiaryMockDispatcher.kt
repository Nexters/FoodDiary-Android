package com.nexters.fooddiary.data.mock

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class FoodDiaryMockDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return MockResponse().setResponseCode(404)
        val method = request.method

        return try {
            when {
                // Auth
                path == MockUrlConfig.PATH_LOGIN && method == "POST" -> {
                    success(MockUrlConfig.MOCK_LOGIN_SUCCESS)
                }
                
                // Diary
                path == MockUrlConfig.PATH_DIARIES && method == "POST" -> {
                    success(MockUrlConfig.MOCK_CREATE_DIARY_SUCCESS)
                }
                path.matches(Regex(MockUrlConfig.REGEX_DIARIES_QUERY)) && method == "GET" -> {
                    success(MockUrlConfig.MOCK_GET_DIARY_20260117)
                }
                path.matches(Regex(MockUrlConfig.REGEX_DIARIES_SUMMARY_QUERY)) && method == "GET" -> {
                    success(MockUrlConfig.MOCK_GET_DIARY_SUMMARY_20260222)
                }

                // Photo
                path == MockUrlConfig.PATH_PHOTOS && method == "POST" -> {
                    success(MockUrlConfig.MOCK_UPLOAD_URL_SUCCESS)
                }
                path.matches(Regex(MockUrlConfig.REGEX_PHOTO_ANALYZE)) && method == "POST" -> {
                    success(MockUrlConfig.MOCK_ANALYZE_PHOTO_SUCCESS)
                }
                 path.matches(Regex(MockUrlConfig.REGEX_PHOTO_ANALYSIS)) && method == "GET" -> {
                    success(MockUrlConfig.MOCK_GET_ANALYSIS_RESULT)
                }
                path.matches(Regex(MockUrlConfig.REGEX_PHOTO_CONFIRM)) && method == "POST" -> {
                    success(MockUrlConfig.MOCK_CONFIRM_PHOTO_SUCCESS)
                }
                 path.matches(Regex(MockUrlConfig.REGEX_PHOTO_FINAL)) && method == "GET" -> {
                    success(MockUrlConfig.MOCK_GET_FINAL_RECORD)
                }

                else -> MockResponse().setResponseCode(404)
            }
        } catch (e: Exception) {
            e.printStackTrace()
             MockResponse().setResponseCode(500)
        }
    }

    private fun success(fileName: String): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody(MockResponseFileReader.readFile(fileName))
            .addHeader("Content-Type", "application/json")
    }
}
