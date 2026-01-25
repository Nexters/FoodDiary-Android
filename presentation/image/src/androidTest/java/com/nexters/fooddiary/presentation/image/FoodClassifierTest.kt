package com.nexters.fooddiary.presentation.image

import android.content.Context
import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nexters.fooddiary.core.classification.FoodClassifier
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Rule
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
internal class FoodClassifierTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var classifier: FoodClassifier

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testThroughput() = runBlocking {
        val testImageCount = 50
        val testBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.RGB_565)
        testBitmap.eraseColor(android.graphics.Color.RED)

        val totalTime = measureTimeMillis {
            repeat(testImageCount) { index ->
                val result = classifier.classifyAsync(testBitmap)
                assertNotNull("Result should not be null at index $index", result)
            }
        }

        val averageTime = totalTime.toDouble().div(testImageCount)
        val throughput = (testImageCount * 1000.0).div(totalTime)

        println("=== 처리량 테스트 결과 ===")
        println("총 처리 이미지: $testImageCount 개")
        println("총 소요 시간: ${totalTime}ms")
        println("평균 처리 시간: ${String.format("%.2f", averageTime)}ms")
        println("처리량: ${String.format("%.2f", throughput)} images/sec")

        assertTrue(
            "Average processing time should be reasonable (<= 100ms), but was ${averageTime}ms",
            averageTime <= 100.0
        )
    }

    @Test
    fun testLoadConcurrentRequests() = runBlocking {
        val concurrentRequests = 5
        val testBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.RGB_565)
        testBitmap.eraseColor(android.graphics.Color.BLUE)

        val results: List<Triple<Int, com.nexters.fooddiary.core.classification.FoodClassificationResult, Long>>
        val totalTime = measureTimeMillis {
            results = (1..concurrentRequests).map { index ->
                async(Dispatchers.Default) {
                    try {
                        val startTime = System.currentTimeMillis()
                        val result = classifier.classifyAsync(testBitmap)
                        val endTime = System.currentTimeMillis()
                        
                        Triple(index, result, endTime - startTime)
                    } catch (e: Exception) {
                        println("Request $index failed: ${e.message}")
                        throw e
                    }
                }
            }.awaitAll()
        }

        results.forEach { (index, result, _) ->
            assertNotNull("Result should not be null for request $index", result)
            assertTrue("Result should have valid confidence values", result.foodConfidence >= 0f)
            assertTrue("Result should have valid confidence values", result.notFoodConfidence >= 0f)
        }

        val avgTime = results.map { it.third }.average()
        val maxTime = results.map { it.third }.maxOrNull() ?: 0L
        val minTime = results.map { it.third }.minOrNull() ?: 0L

        println("=== 부하 테스트 결과 (동시 요청) ===")
        println("동시 요청 수: $concurrentRequests")
        println("총 소요 시간: ${totalTime}ms")
        println("평균 응답 시간: ${String.format("%.2f", avgTime)}ms")
        println("최대 응답 시간: ${maxTime}ms")
        println("최소 응답 시간: ${minTime}ms")

        assertTrue(
            "Concurrent requests should complete in reasonable time (<= 5000ms), but was ${totalTime}ms",
            totalTime <= 5000
        )
    }

    @Test
    fun testLoadSequentialRequests() = runBlocking {
        val requestCount = 100
        val testBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.RGB_565)
        testBitmap.eraseColor(android.graphics.Color.GREEN)

        val times = mutableListOf<Long>()
        val totalTime = measureTimeMillis {
            repeat(requestCount) { index ->
                val requestTime = measureTimeMillis {
                    val result = classifier.classifyAsync(testBitmap)
                    assertNotNull("Result should not be null at index $index", result)
                }
                times.add(requestTime)
            }
        }

        val avgTime = times.average()
        val maxTime = times.maxOrNull() ?: 0L
        val minTime = times.minOrNull() ?: 0L
        val throughput = (requestCount * 1000.0).div(totalTime)

        println("=== 부하 테스트 결과 (연속 요청) ===")
        println("연속 요청 수: $requestCount")
        println("총 소요 시간: ${totalTime}ms")
        println("평균 응답 시간: ${String.format("%.2f", avgTime)}ms")
        println("최대 응답 시간: ${maxTime}ms")
        println("최소 응답 시간: ${minTime}ms")
        println("처리량: ${String.format("%.2f", throughput)} requests/sec")

        assertTrue(
            "Average time should remain reasonable under load (<= 100ms), but was ${avgTime}ms",
            avgTime <= 100.0
        )
    }
}
