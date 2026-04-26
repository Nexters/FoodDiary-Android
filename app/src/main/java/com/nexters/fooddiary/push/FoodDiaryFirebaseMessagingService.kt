package com.nexters.fooddiary.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexters.fooddiary.R
import com.nexters.fooddiary.domain.usecase.SyncDeviceTokenUseCase
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

@AndroidEntryPoint
class FoodDiaryFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var syncDeviceTokenUseCase: SyncDeviceTokenUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data[TYPE_DATA_KEY].orEmpty()
        val diaryDate = message.data[DIARY_DATE_DATA_KEY].orEmpty()

        if (type == FoodDiaryPushTypes.ANALYSIS_COMPLETE && diaryDate.isNotBlank()) {
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
        private const val DIARY_DATE_DATA_KEY = "diary_date"
        private const val TYPE_DATA_KEY = "type"
    }

    private fun showNotification(message: RemoteMessage) {
        val type = message.data[TYPE_DATA_KEY].orEmpty()
        val diaryDate = message.data[DIARY_DATE_DATA_KEY].orEmpty()
        val channelId = message.notification?.channelId
            ?.takeIf { it.isNotBlank() }
            ?: FoodDiaryNotificationHelper.DEFAULT_CHANNEL_ID

        val title = message.notification?.title
            ?.takeIf { it.isNotBlank() }
            ?: getDefaultTitle(type)
        val body = message.notification?.body
            ?.takeIf { it.isNotBlank() }
            ?: getDefaultBody(type, diaryDate)

        FoodDiaryNotificationHelper.show(
            context = this,
            channelId = channelId,
            title = title,
            body = body,
            contentIntent = createLaunchIntent(type, diaryDate),
            notificationId = (type + diaryDate + title).hashCode(),
        )
    }

    private fun getDefaultTitle(type: String): String {
        return when (type) {
            FoodDiaryPushTypes.ANALYSIS_COMPLETE -> getString(R.string.push_analysis_complete_title)
            FoodDiaryPushTypes.DAILY_RECORD_REMINDER -> getString(R.string.push_daily_record_reminder_title)
            else -> getString(R.string.push_default_title)
        }
    }

    private fun getDefaultBody(type: String, diaryDate: String): String {
        return when (type) {
            FoodDiaryPushTypes.ANALYSIS_COMPLETE -> {
                val displayDate = formatDiaryDate(diaryDate) ?: diaryDate
                if (displayDate.isBlank()) {
                    getString(R.string.push_analysis_complete_body_no_date)
                } else {
                    getString(R.string.push_analysis_complete_body, displayDate)
                }
            }

            FoodDiaryPushTypes.DAILY_RECORD_REMINDER -> getString(R.string.push_daily_record_reminder_body)
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

    private fun createLaunchIntent(type: String, diaryDate: String) = when (type) {
        FoodDiaryPushTypes.DAILY_RECORD_REMINDER -> {
            FoodDiaryNotificationHelper.createHomeLaunchIntent(this, type)
        }

        else -> {
            FoodDiaryNotificationHelper.createDetailLaunchIntent(
                context = this,
                pushType = type,
                diaryDate = diaryDate
            )
        }
    }
}
