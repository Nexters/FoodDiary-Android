package com.nexters.fooddiary.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FoodDiaryFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val dataPayload = if (message.data.isEmpty()) "{}" else {
            message.data.entries.joinToString(
                prefix = "{",
                postfix = "}",
                separator = ", "
            ) { (key, value) -> "\"$key\":\"$value\"" }
        }

        val notification = message.notification
        Log.i(
            TAG,
            """
            FCM message received
            from=${message.from}
            messageId=${message.messageId}
            collapseKey=${message.collapseKey}
            sentTime=${message.sentTime}
            ttl=${message.ttl}
            priority=${message.priority}
            originalPriority=${message.originalPriority}
            notification.title=${notification?.title}
            notification.body=${notification?.body}
            notification.channelId=${notification?.channelId}
            data=$dataPayload
            """.trimIndent()
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: 서버 토큰 동기화 API가 준비되면 여기서 업로드 처리
        Log.d(TAG, "FCM token refreshed")
    }

    companion object {
        private const val TAG = "FD-FCM-Service"
    }
}
