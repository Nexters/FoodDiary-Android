package com.nexters.fooddiary.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nexters.fooddiary.domain.model.ReviewPromptState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.reviewPromptDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "review_prompt_prefs"
)

@Singleton
class ReviewPromptStore private constructor(
    private val dataStore: DataStore<Preferences>,
) {
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : this(context.reviewPromptDataStore)

    private val successfulRecordCountKey = intPreferencesKey("successful_record_count")
    private val hasRequestedReviewKey = booleanPreferencesKey("has_requested_review")

    suspend fun incrementSuccessfulRecordCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[successfulRecordCountKey] ?: 0
            preferences[successfulRecordCountKey] = currentCount + 1
        }
    }

    suspend fun getState(): ReviewPromptState {
        val preferences = dataStore.data.first()
        return ReviewPromptState(
            successfulRecordCount = preferences[successfulRecordCountKey] ?: 0,
            hasRequestedReview = preferences[hasRequestedReviewKey] ?: false,
        )
    }

    suspend fun markInAppReviewRequested() {
        dataStore.edit { preferences ->
            preferences[hasRequestedReviewKey] = true
        }
    }

    companion object {
        internal fun createForTest(dataStore: DataStore<Preferences>): ReviewPromptStore {
            return ReviewPromptStore(dataStore)
        }
    }
}
