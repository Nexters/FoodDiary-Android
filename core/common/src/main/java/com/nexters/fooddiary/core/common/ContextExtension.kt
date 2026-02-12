package com.nexters.fooddiary.core.common

import android.content.Context
import android.content.pm.PackageManager

object ContextExtension {
    fun Context.getAppVersionName(): String {
        val packageManager = packageManager
        val packageName = packageName

        return if (android.os.Build.VERSION.SDK_INT >= 33) {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
            info.versionName ?: ""
        } else {
            @Suppress("DEPRECATION")
            val info = packageManager.getPackageInfo(packageName, 0)
            info.versionName ?: ""
        }
    }
}