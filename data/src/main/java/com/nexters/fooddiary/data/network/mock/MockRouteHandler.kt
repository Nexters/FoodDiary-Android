package com.nexters.fooddiary.data.network.mock

object MockRouteHandler {

    // Paths
    private const val PATH_LOGIN = "/auth/login"
    private const val PATH_VERIFY = "/auth/verify"
    private const val PATH_DELETE_ME = "/users/me"
    private const val PATH_DIARIES = "/diaries"
    private const val PATH_PHOTOS = "/photos"
    private const val PATH_RESTAURANT_SEARCH = "/restaurant/search"

    // Regex Patterns
    private val REGEX_PHOTO_ANALYZE = Regex("/photos/\\d+/analyze")
    private val REGEX_PHOTO_ANALYSIS = Regex("/photos/\\d+/analysis")
    private val REGEX_PHOTO_CONFIRM = Regex("/photos/\\d+/confirm")
    private val REGEX_PHOTO_FINAL = Regex("/photos/\\d+/final")

    // Mock Files
    private const val MOCK_LOGIN_SUCCESS = "login_success.json"
    private const val MOCK_VERIFY_TOKEN_SUCCESS = "verify_token_success.json"
    private const val MOCK_DELETE_ME_SUCCESS = "delete_me_success.json"
    private const val MOCK_CREATE_DIARY_SUCCESS = "create_diary_success.json"
    private const val MOCK_GET_DIARY_20260117 = "get_diary_20260117.json"
    private const val MOCK_UPLOAD_URL_SUCCESS = "get_upload_url_success.json"
    private const val MOCK_ANALYZE_PHOTO_SUCCESS = "analyze_photo_success.json"
    private const val MOCK_GET_ANALYSIS_RESULT = "get_analysis_result.json"
    private const val MOCK_CONFIRM_PHOTO_SUCCESS = "confirm_photo_success.json"
    private const val MOCK_GET_FINAL_RECORD = "get_final_record.json"
    private const val MOCK_GET_RESTAURANT_SEARCH = "get_restaurant_search.json"

    /**
     * path와 method를 기반으로 mock JSON 파일명을 반환
     * @return mock JSON 파일명, 매칭되는 라우트가 없으면 null
     */
    fun getMockFileName(path: String, method: String): String? {
        return when {
            // Auth
//            path == PATH_LOGIN && method == "POST" -> MOCK_LOGIN_SUCCESS
//            path == PATH_VERIFY && method == "GET" -> MOCK_VERIFY_TOKEN_SUCCESS
//            path == PATH_DELETE_ME && method == "DELETE" -> MOCK_DELETE_ME_SUCCESS

            // Diary - 모든 날짜에 대해 동일 mock 사용 (Interceptor에서 요청 path의 date로 치환)
            path == PATH_DIARIES && method == "POST" -> MOCK_CREATE_DIARY_SUCCESS
//            path == PATH_DIARIES && method == "GET" -> MOCK_GET_DIARY_20260117

            // Photo
            path == PATH_PHOTOS && method == "POST" -> MOCK_UPLOAD_URL_SUCCESS
            path.matches(REGEX_PHOTO_ANALYZE) && method == "POST" -> MOCK_ANALYZE_PHOTO_SUCCESS
            path.matches(REGEX_PHOTO_ANALYSIS) && method == "GET" -> MOCK_GET_ANALYSIS_RESULT
            path.matches(REGEX_PHOTO_CONFIRM) && method == "POST" -> MOCK_CONFIRM_PHOTO_SUCCESS
            path.matches(REGEX_PHOTO_FINAL) && method == "GET" -> MOCK_GET_FINAL_RECORD

            // Restaurant
//            path == PATH_RESTAURANT_SEARCH && method == "GET" -> MOCK_GET_RESTAURANT_SEARCH

            else -> null
        }
    }
}
