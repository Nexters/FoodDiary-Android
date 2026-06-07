package com.nexters.fooddiary.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewPromptStoreTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `성공 카운트와 리뷰 요청 여부를 저장한다`() = runTest {
        val store = createStore()

        store.incrementSuccessfulRecordCount()
        var state = store.getState()
        assertEquals(1, state.successfulRecordCount)
        assertFalse(state.hasRequestedReview)

        store.incrementSuccessfulRecordCount()
        store.markInAppReviewRequested()

        state = store.getState()
        assertEquals(2, state.successfulRecordCount)
        assertTrue(state.hasRequestedReview)
    }

    private fun TestScope.createStore(): ReviewPromptStore {
        val file = temporaryFolder.newFile("review_prompt.preferences_pb")
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            scope = backgroundScope,
            produceFile = { file.absolutePath.toPath() },
        )
        return ReviewPromptStore.createForTest(dataStore)
    }
}
