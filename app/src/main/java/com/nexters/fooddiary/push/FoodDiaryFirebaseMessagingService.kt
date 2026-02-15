package com.nexters.fooddiary.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FoodDiaryFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: 서버 토큰 동기화 API가 준비되면 여기서 업로드 처리
        Log.d(TAG, "FCM token refreshed")
    }

    companion object {
        private const val TAG = "FD-FCM-Service"
    }
}
