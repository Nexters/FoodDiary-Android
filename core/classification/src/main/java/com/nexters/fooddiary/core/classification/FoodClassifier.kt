package com.nexters.fooddiary.core.classification

import android.content.Context
import android.graphics.Bitmap
import com.nexters.fooddiary.core.classification.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class FoodClassifier private constructor(
    private val interpreter: Interpreter
) {
    companion object {
        private const val OUTPUT_SIZE = 2
        private const val OUTPUT_BATCH_SIZE = 1

        @Throws(IOException::class)
        fun create(context: Context): FoodClassifier {
            val modelBuffer = loadModelFile(context)
            val options = Interpreter.Options().apply {
                setNumThreads(1)
                setUseXNNPACK(false)
            }
            val interpreter = Interpreter(modelBuffer, options)
            return FoodClassifier(interpreter)
        }

        private fun loadModelFile(context: Context): MappedByteBuffer {
            val modelFileName = context.getString(R.string.model_file_name)
            return context.assets.openFd(modelFileName).use { fileDescriptor ->
                FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                    val fileChannel = inputStream.channel
                    val startOffset = fileDescriptor.startOffset
                    val declaredLength = fileDescriptor.declaredLength
                    fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
                }
            }
        }
    }

    private val mutex = Mutex()

    private fun classify(bitmap: Bitmap): FoodClassificationResult {
        val inputBuffer = ImagePreprocessor.preprocessImage(bitmap)
        val outputBuffer = Array(OUTPUT_BATCH_SIZE) { ByteArray(OUTPUT_SIZE) }

        interpreter.run(inputBuffer, outputBuffer)

        return FoodClassificationResult.fromModelOutput(outputBuffer[0])
    }

    suspend fun classifyAsync(bitmap: Bitmap): FoodClassificationResult = 
        withContext(Dispatchers.Default) {
            mutex.withLock {
                classify(bitmap)
            }
        }

    fun close() {
        interpreter.close()
    }
}
