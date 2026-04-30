package com.nexters.fooddiary.core.common.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtil {
    enum class MediaAccessState {
        FULL,
        PARTIAL,
        DENIED,
    }

    fun getRequiredMediaPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    fun getRequiredMediaPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            }

            else -> {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    fun getMediaAccessState(context: Context): MediaAccessState {
        val hasFullAccess = ContextCompat.checkSelfPermission(
            context,
            getRequiredMediaPermission()
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFullAccess) return MediaAccessState.FULL

        val hasPartialAccess = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED

        return if (hasPartialAccess) {
            MediaAccessState.PARTIAL
        } else {
            MediaAccessState.DENIED
        }
    }

    // 전체 앨범 접근 권한이 허용되었으면 true, 아니면 false
    fun hasMediaPermission(context: Context): Boolean {
        return getMediaAccessState(context) == MediaAccessState.FULL
    }

    fun hasPartialMediaPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        ) == PackageManager.PERMISSION_GRANTED
    }
}
