package com.nexters.fooddiary.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nexters.fooddiary.R

class DailyRecordReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            DailyRecordReminderScheduler.ACTION_DAILY_RECORD_REMINDER -> handleDailyRecordReminder(context)
        }
    }

    private fun handleDailyRecordReminder(context: Context) {
        if (DailyRecordReminderScheduler.isReminderEnabled(context)) {
            showDailyRecordReminder(context)
        }
        DailyRecordReminderScheduler.scheduleNext(context)
    }

    private fun showDailyRecordReminder(context: Context) {
        val title = context.getString(R.string.push_daily_record_reminder_title)
        val body = context.getString(R.string.push_daily_record_reminder_body)
        FoodDiaryNotificationHelper.show(
            context = context,
            title = title,
            body = body,
            contentIntent = FoodDiaryNotificationHelper.createHomeLaunchIntent(
                context = context,
                pushType = FoodDiaryPushTypes.DAILY_RECORD_REMINDER
            ),
            notificationId = FoodDiaryPushTypes.DAILY_RECORD_REMINDER.hashCode(),
        )
    }
}
