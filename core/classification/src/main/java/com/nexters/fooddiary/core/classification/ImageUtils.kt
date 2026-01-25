package com.nexters.fooddiary.core.classification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.nexters.fooddiary.core.classification.R

object ImageUtils {
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val decodedBitmap = decodeBitmapFromUri(context, uri)
            decodedBitmap?.let { ImageRotationHelper.correctOrientation(context, it, uri) }
        } catch (e: Exception) {
            null
        }
    }

    private fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            
            val maxDimension = context.resources.getInteger(R.integer.image_max_dimension)
            val sampleSize = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                maxDimension,
                context
            )
            
            options.apply {
                inJustDecodeBounds = false
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            
            val decodeStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(decodeStream, null, options)
            decodeStream.close()
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        maxDimension: Int,
        context: Context
    ): Int {
        var inSampleSize = 1
        
        if (width > maxDimension || height > maxDimension) {
            val halfWidth = width.div(2)
            val halfHeight = height.div(2)
            val multiplier = context.resources.getInteger(R.integer.image_sample_size_multiplier)
            
            while ((halfWidth.div(inSampleSize) >= maxDimension) &&
                   (halfHeight.div(inSampleSize) >= maxDimension)) {
                inSampleSize *= multiplier
            }
        }
        
        return inSampleSize
    }
}

