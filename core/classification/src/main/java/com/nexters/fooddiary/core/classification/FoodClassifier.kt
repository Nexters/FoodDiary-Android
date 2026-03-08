package com.nexters.fooddiary.core.classification

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.internal.DoubleCheck.lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodClassifier @Inject constructor(
    @ApplicationContext context: Context
) {
    private val OUTPUT_SIZE = 2
    private val OUTPUT_BATCH_SIZE = 1
    private val interpreter: Interpreter = Interpreter(
        loadModelFile(context),
        Interpreter.Options().apply {
            setNumThreads(1)
            setUseXNNPACK(false)
        }
    )

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
