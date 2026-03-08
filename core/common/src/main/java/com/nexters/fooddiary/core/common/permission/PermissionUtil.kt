package com.nexters.fooddiary.core.common.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtil {

    fun getRequiredMediaPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    // 권한이 허용되었으면 true, 아니면 false
    fun hasMediaPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            getRequiredMediaPermission()
        ) == PackageManager.PERMISSION_GRANTED
    }
}
