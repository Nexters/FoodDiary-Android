package com.nexters.fooddiary.core.classification

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal object ImagePreprocessor {
    private const val IMAGE_SIZE = 224
    private const val RGB_CHANNELS = 3
    private const val BYTES_PER_CHANNEL = 1

    private const val BUFFER_SIZE = IMAGE_SIZE * IMAGE_SIZE * RGB_CHANNELS * BYTES_PER_CHANNEL

    private const val RED_MASK = 0xFF0000
    private const val GREEN_MASK = 0xFF00
    private const val BLUE_MASK = 0xFF

    private const val RED_SHIFT = 16
    private const val GREEN_SHIFT = 8
    private const val BLUE_SHIFT = 0

    fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = bitmap.resizeToModelInput()
        val byteBuffer = createByteBuffer()

        resizedBitmap.extractRgbToBuffer(byteBuffer)
        byteBuffer.rewind()

        return byteBuffer
    }

    private fun Bitmap.resizeToModelInput(): Bitmap =
        when {
            width == IMAGE_SIZE && height == IMAGE_SIZE -> this
            else -> Bitmap.createScaledBitmap(this, IMAGE_SIZE, IMAGE_SIZE, true)
        }

    private fun createByteBuffer(): ByteBuffer =
        ByteBuffer.allocateDirect(BUFFER_SIZE).apply {
            order(ByteOrder.nativeOrder())
        }

    private fun Bitmap.extractRgbToBuffer(buffer: ByteBuffer) {
        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        for (pixel in pixels) {
            val r = (pixel and RED_MASK) shr RED_SHIFT
            val g = (pixel and GREEN_MASK) shr GREEN_SHIFT
            val b = (pixel and BLUE_MASK) shr BLUE_SHIFT

            buffer.put(r.toByte())
            buffer.put(g.toByte())
            buffer.put(b.toByte())
        }
    }
}
