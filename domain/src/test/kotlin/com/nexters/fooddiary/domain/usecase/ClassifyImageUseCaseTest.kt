package com.nexters.fooddiary.domain.usecase

import com.nexters.fooddiary.domain.model.ClassificationResult
import com.nexters.fooddiary.domain.repository.ClassificationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ClassifyImageUseCaseTest {

    @Test
    fun `같은 URI로 두 번 호출 시 캐시된 결과가 반환된다`() = runTest {
        val fakeRepo = FakeClassificationRepository()
        val useCase = ClassifyImageUseCase(fakeRepo)
        val uri = "content://media/external/images/media/123"

        val result1 = useCase(uri)
        val result2 = useCase(uri)

        assertNotNull(result1)
        assertNotNull(result2)
        assertEquals(result1, result2)
        assertEquals(1, fakeRepo.classifyCallCount)
    }

    @Test
    fun `다른 URI로 호출 시 각각 분류가 수행된다`() = runTest {
        val fakeRepo = FakeClassificationRepository()
        val useCase = ClassifyImageUseCase(fakeRepo)

        useCase("content://media/external/images/media/1")
        useCase("content://media/external/images/media/2")

        assertEquals(2, fakeRepo.classifyCallCount)
    }

    private class FakeClassificationRepository : ClassificationRepository {
        var classifyCallCount = 0
        private val cache = mutableMapOf<String, ClassificationResult>()

        override suspend fun classifyImage(uriString: String): ClassificationResult? {
            cache[uriString]?.let {
                return it
            }
            classifyCallCount++
            val result = ClassificationResult(
                isFood = true,
                foodConfidence = 0.9f,
                notFoodConfidence = 0.1f
            )
            cache[uriString] = result
            return result
        }
    }
}
