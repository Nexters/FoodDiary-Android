package com.nexters.fooddiary.push

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalTime
import java.time.ZonedDateTime

internal object DailyRecordReminderScheduler {
    const val ACTION_DAILY_RECORD_REMINDER =
        "com.nexters.fooddiary.action.DAILY_RECORD_REMINDER"

    private const val REQUEST_CODE = 329
    private const val PREFS_NAME = "daily_record_reminder"
    private const val KEY_REMINDER_ENABLED = "reminder_enabled"
    private val reminderTime = LocalTime.of(21, 0)

    fun setReminderEnabled(context: Context, enabled: Boolean) {
        context.reminderPreferences.edit()
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .apply()

        if (enabled) {
            scheduleNext(context)
        } else {
            cancel(context)
        }
    }

    fun scheduleNext(context: Context) {
        if (!isReminderEnabled(context)) return

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerAtMillis = nextTriggerAtMillis(ZonedDateTime.now())
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            createPendingIntent(context)
        )
    }

    internal fun nextTriggerAtMillis(now: ZonedDateTime): Long {
        val todayReminder = now
            .withHour(reminderTime.hour)
            .withMinute(reminderTime.minute)
            .withSecond(0)
            .withNano(0)
        val nextReminder = if (now.isBefore(todayReminder)) {
            todayReminder
        } else {
            todayReminder.plusDays(1)
        }
        return nextReminder.toInstant().toEpochMilli()
    }

    internal fun isReminderEnabled(context: Context): Boolean {
        return context.reminderPreferences.getBoolean(KEY_REMINDER_ENABLED, false)
    }

    private fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(createPendingIntent(context))
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyRecordReminderReceiver::class.java).apply {
            action = ACTION_DAILY_RECORD_REMINDER
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val Context.reminderPreferences
        get() = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
