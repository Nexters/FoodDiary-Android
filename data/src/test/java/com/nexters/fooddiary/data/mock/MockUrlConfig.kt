package com.nexters.fooddiary.data.mock

object MockUrlConfig {
    // Paths
    const val PATH_LOGIN = "/auth/login"
    const val PATH_DIARIES = "/diaries"
    const val PATH_PHOTOS = "/photos"
    const val PATH_RESTAURANT_SEARCH = "/restaurant/search"
    
    // Regex Patterns
    const val REGEX_DIARY_DATE = "/diaries/\\d{4}-\\d{2}-\\d{2}"
    const val REGEX_PHOTO_ANALYZE = "/photos/\\d+/analyze"
    const val REGEX_PHOTO_ANALYSIS = "/photos/\\d+/analysis"
    const val REGEX_PHOTO_CONFIRM = "/photos/\\d+/confirm"
    const val REGEX_PHOTO_FINAL = "/photos/\\d+/final"

    // Mock Files
    const val MOCK_LOGIN_SUCCESS = "login_success.json"
    const val MOCK_CREATE_DIARY_SUCCESS = "create_diary_success.json"
    const val MOCK_GET_DIARY_20260117 = "get_diary_20260117.json"
    const val MOCK_UPLOAD_URL_SUCCESS = "get_upload_url_success.json"
    const val MOCK_ANALYZE_PHOTO_SUCCESS = "analyze_photo_success.json"
    const val MOCK_GET_ANALYSIS_RESULT = "get_analysis_result.json"
    const val MOCK_CONFIRM_PHOTO_SUCCESS = "confirm_photo_success.json"
    const val MOCK_GET_FINAL_RECORD = "get_final_record.json"
    const val MOCK_GET_RESTAURANT_SEARCH = "get_restaurant_search.json"
}
