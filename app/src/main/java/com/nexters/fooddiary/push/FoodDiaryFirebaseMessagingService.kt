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
import com.nexters.fooddiary.domain.usecase.SyncDeviceTokenUseCase
import com.nexters.fooddiary.navigation.NavigationConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject
import com.nexters.fooddiary.core.ui.R as coreR

@AndroidEntryPoint
class FoodDiaryFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var syncDeviceTokenUseCase: SyncDeviceTokenUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data["type"].orEmpty()
        val diaryDate = message.data["diary_date"].orEmpty()

        if (type == ANALYSIS_COMPLETE_TYPE && diaryDate.isNotBlank()) {
            PushSyncEventBus.publishAnalysisComplete(diaryDate)
        }

        showNotification(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (token.isBlank()) return

        serviceScope.launch {
            syncDeviceTokenUseCase(token)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
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
            val date = runCatching { LocalDate.parse(rawDate) }
                .getOrElse { LocalDateTime.parse(rawDate).toLocalDate() }
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
