package com.nexters.fooddiary.data.mapper

import com.nexters.fooddiary.data.local.upload.UploadStatus
import com.nexters.fooddiary.data.remote.photo.model.response.BatchUploadDiaryItem

fun BatchUploadDiaryItem.toUploadStatus(): UploadStatus? = when (diaryStatus) {
    DiaryStatus.FAILED -> UploadStatus.FAILURE
    DiaryStatus.PROCESSING -> UploadStatus.PENDING
    DiaryStatus.DONE -> null
    else -> UploadStatus.PENDING
}

private object DiaryStatus {
    const val PROCESSING = "processing"
    const val DONE = "done"
    const val FAILED = "failed"
}

