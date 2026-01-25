package com.nexters.fooddiary.core.classification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

object ImageRotationHelper {
    private const val NO_ROTATION = 0f
    private const val HALF_DIVISOR = 2f

    fun correctOrientation(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
        val orientation = getOrientation(context, uri)
        
        return when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> bitmap.flipHorizontally()
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> bitmap.flipVertically()
            ExifInterface.ORIENTATION_TRANSPOSE -> bitmap.rotate(90f).flipHorizontally()
            ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> bitmap.rotate(270f).flipHorizontally()
            ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
            else -> bitmap
        }
    }
    
    private fun getOrientation(context: Context, uri: Uri): Int =
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                ExifInterface(inputStream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    
    private fun Bitmap.rotate(degrees: Float): Bitmap {
        if (degrees == NO_ROTATION) return this
        
        val matrix = Matrix().apply {
            postRotate(degrees)
        }
        
        val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (rotated != this && !isRecycled) {
            recycle()
        }
        return rotated
    }
    
    private fun Bitmap.flipHorizontally(): Bitmap {
        val flipScale = -1f
        val noScale = 1f
        val centerX = width.div(HALF_DIVISOR)
        val centerY = height.div(HALF_DIVISOR)
        
        val matrix = Matrix().apply {
            postScale(flipScale, noScale, centerX, centerY)
        }
        
        val flipped = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        val isNeededToRecycle = flipped != this && !isRecycled

        if (isNeededToRecycle) recycle()

        return flipped
    }
    
    private fun Bitmap.flipVertically(): Bitmap {
        val noScale = 1f
        val flipScale = -1f
        val centerX = width.div(HALF_DIVISOR)
        val centerY = height.div(HALF_DIVISOR)
        
        val matrix = Matrix().apply {
            postScale(noScale, flipScale, centerX, centerY)
        }
        
        val flipped = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (flipped != this && !isRecycled) recycle()

        return flipped
    }
}

