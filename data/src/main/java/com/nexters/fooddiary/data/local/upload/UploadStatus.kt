package com.nexters.fooddiary.data.local.upload

/**
 * 서버 포토 업로드 결과 상태.
 * - PENDING: 200 응답 수신 (업로드 성공)
 * - FAILURE: 업로드 실패
 */
enum class UploadStatus {
    PENDING,
    FAILURE
}
