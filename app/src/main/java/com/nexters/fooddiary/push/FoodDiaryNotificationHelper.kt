package com.nexters.fooddiary.push

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.nexters.fooddiary.MainActivity
import com.nexters.fooddiary.R
import com.nexters.fooddiary.navigation.NavigationConstants
import com.nexters.fooddiary.core.ui.R as coreR

internal object FoodDiaryNotificationHelper {
    const val DEFAULT_CHANNEL_ID = "food_diary_general"

    fun show(
        context: Context,
        channelId: String = DEFAULT_CHANNEL_ID,
        title: String,
        body: String,
        contentIntent: Intent,
        notificationId: Int,
    ) {
        if (!context.hasNotificationPermission()) return

        context.ensureNotificationChannel(channelId)

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(coreR.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, coreR.drawable.ic_app_icon))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun createHomeLaunchIntent(context: Context, pushType: String): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = Uri.Builder()
                .scheme(NavigationConstants.DEEP_LINK_SCHEME)
                .authority(NavigationConstants.DEEP_LINK_HOST_HOME)
                .build()
            putExtra(FoodDiaryPushExtras.PUSH_TYPE, pushType)
        }
    }

    fun createDetailLaunchIntent(
        context: Context,
        pushType: String,
        diaryDate: String,
    ): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = Uri.Builder()
                .scheme(NavigationConstants.DEEP_LINK_SCHEME)
                .authority(NavigationConstants.DEEP_LINK_HOST_DETAIL)
                .appendQueryParameter(NavigationConstants.DEEP_LINK_QUERY_DATE, diaryDate)
                .build()
            putExtra(FoodDiaryPushExtras.PUSH_TYPE, pushType)
            putExtra(FoodDiaryPushExtras.PUSH_DIARY_DATE, diaryDate)
        }
    }

    private fun Context.ensureNotificationChannel(channelId: String) {
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

    private fun Context.hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
