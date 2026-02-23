package com.nexters.fooddiary.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexters.fooddiary.MainActivity
import com.nexters.fooddiary.R
import com.nexters.fooddiary.navigation.NavigationConstants
import java.time.LocalDate
import java.time.format.DateTimeParseException
import com.nexters.fooddiary.core.ui.R as coreR

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

        showNotification(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: 서버 토큰 동기화 API가 준비되면 여기서 업로드 처리
        Log.d(TAG, "FCM token refreshed")
    }

    companion object {
        private const val TAG = "FD-FCM-Service"
        private const val DEFAULT_CHANNEL_ID = "food_diary_general"
        private const val ANALYSIS_COMPLETE_TYPE = "analysis_complete"
    }

    private fun showNotification(message: RemoteMessage) {
        if (!hasNotificationPermission()) {
            return
        }

        val type = message.data["type"].orEmpty()
        val diaryDate = message.data["diary_date"].orEmpty()
        val channelId = message.notification?.channelId
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_CHANNEL_ID

        ensureNotificationChannel(channelId)

        val title = message.notification?.title
            ?.takeIf { it.isNotBlank() }
            ?: getDefaultTitle(type)
        val body = message.notification?.body
            ?.takeIf { it.isNotBlank() }
            ?: getDefaultBody(type, diaryDate)

        val pendingIntent = PendingIntent.getActivity(
            this,
            (type + diaryDate).hashCode(),
            createLaunchIntent(type, diaryDate),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(coreR.drawable.ic_app_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify((type + diaryDate + title).hashCode(), notification)
    }

    private fun createLaunchIntent(type: String, diaryDate: String): Intent {
        val deepLink = Uri.Builder()
            .scheme("fooddiary")
            .authority(NavigationConstants.DEEP_LINK_HOST_DETAIL)
            .appendQueryParameter(NavigationConstants.DEEP_LINK_QUERY_DATE, diaryDate)
            .build()

        return Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = deepLink
            putExtra("push_type", type)
            putExtra("push_diary_date", diaryDate)
        }
    }

    private fun getDefaultTitle(type: String): String {
        return when (type) {
            ANALYSIS_COMPLETE_TYPE -> getString(R.string.push_analysis_complete_title)
            else -> getString(R.string.push_default_title)
        }
    }

    private fun getDefaultBody(type: String, diaryDate: String): String {
        return when (type) {
            ANALYSIS_COMPLETE_TYPE -> {
                val displayDate = formatDiaryDate(diaryDate) ?: diaryDate
                if (displayDate.isBlank()) {
                    getString(R.string.push_analysis_complete_body_no_date)
                } else {
                    getString(R.string.push_analysis_complete_body, displayDate)
                }
            }

            else -> getString(R.string.push_default_body)
        }
    }

    private fun formatDiaryDate(rawDate: String): String? {
        if (rawDate.isBlank()) return null
        return try {
            val date = LocalDate.parse(rawDate)
            "${date.monthValue}월 ${date.dayOfMonth}일"
        } catch (_: DateTimeParseException) {
            rawDate
        }
    }

    private fun ensureNotificationChannel(channelId: String) {
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(channelId) != null) return

        val channel = NotificationChannel(
            channelId,
            getString(R.string.push_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.push_channel_description)
        }
        manager.createNotificationChannel(channel)
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
