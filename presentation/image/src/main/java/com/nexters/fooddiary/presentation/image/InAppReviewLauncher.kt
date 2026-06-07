package com.nexters.fooddiary.presentation.image

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.tasks.await

internal interface InAppReviewLauncher {
    suspend fun launch(): Result<Unit>
}

internal class GooglePlayInAppReviewLauncher(
    private val context: Context,
) : InAppReviewLauncher {
    override suspend fun launch(): Result<Unit> {
        val activity = context.findActivity()
            ?: return Result.failure(IllegalStateException("Activity context is required"))

        return runCatching {
            val reviewManager = ReviewManagerFactory.create(context)
            val reviewInfo = reviewManager.requestReviewFlow().await()
            reviewManager.launchReviewFlow(activity, reviewInfo).await()
            Log.d(TAG, "Play in-app review flow launch completed")
            Unit
        }.onFailure {
            Log.w(TAG, "Play in-app review flow launch failed", it)
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val TAG = "InAppReviewLauncher"
