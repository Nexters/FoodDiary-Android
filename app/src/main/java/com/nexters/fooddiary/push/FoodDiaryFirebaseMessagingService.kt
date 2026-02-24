package com.nexters.fooddiary.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexters.fooddiary.domain.repository.PhotoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FoodDiaryFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var photoRepository: PhotoRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        serviceScope.launch {
            runCatching { photoRepository.clearPendingUploads() }
                .onFailure { e ->
                    Log.w(TAG, "clearPendingUploads failed (db may be closed if process was killed)", e)
                }
        }

        val dataPayload = if (message.data.isEmpty()) "{}" else {
            message.data.entries.joinToString(
                prefix = "{",
                postfix = "}",
                separator = ", "
            ) { (key, value) -> "\"$key\":\"$value\"" }
        }

        val notification = message.notification

        //TODO FCM 메세지 확인 이후 제거 필수
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
        // Photo 업로드/FCM/DB 관련 로그를 한 태그로 모으기 위함
        private const val TAG = "FD-PhotoUpload"
    }
}
