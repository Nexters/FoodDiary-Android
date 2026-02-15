package com.nexters.fooddiary.data.firebase

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.nexters.fooddiary.core.common.ContextExtension.getAppVersionName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class LoginDeviceInfo(
    val appVersion: String,
    val deviceId: String,
    val deviceToken: String,
    val isActive: Boolean,
    val osVersion: String
)

interface LoginDeviceInfoProvider {
    suspend fun getLoginDeviceInfo(): LoginDeviceInfo
}

@Singleton
class AndroidLoginDeviceInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginFirebaseTokenProvider: LoginFirebaseTokenProvider
) : LoginDeviceInfoProvider {
    override suspend fun getLoginDeviceInfo(): LoginDeviceInfo {
        val deviceId = loginFirebaseTokenProvider.getInstallationId()

        return LoginDeviceInfo(
            appVersion = context.getAppVersionName(),
            deviceId = deviceId,
            deviceToken = loginFirebaseTokenProvider.getDeviceToken(),
            isActive = NotificationManagerCompat.from(context).areNotificationsEnabled(),
            osVersion = Build.VERSION.RELEASE.orEmpty()
        )
    }
}
