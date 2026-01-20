package com.nexters.fooddiary.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nexters.fooddiary.data.security.AesEncryption
import com.nexters.fooddiary.data.security.EncryptionKeyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "token_prefs")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptionKeyManager: EncryptionKeyManager
) {
    private val dataStore: DataStore<Preferences> = context.tokenDataStore
    
    private val TOKEN_KEY = stringPreferencesKey("encrypted_auth_token")

    suspend fun saveToken(token: String) {
        val encryptionKey = encryptionKeyManager.getOrCreateKey()
        val encryptedToken = AesEncryption.encrypt(token, encryptionKey)
        
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = encryptedToken
        }
    }

    suspend fun getToken(): String? {
        return try {
            val preferences: Preferences = dataStore.data.first()
            preferences[TOKEN_KEY]?.let { encryptedToken ->
                val encryptionKey = encryptionKeyManager.getOrCreateKey()
                AesEncryption.decrypt(encryptedToken, encryptionKey)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getTokenFlow(): Flow<String?> {
        return dataStore.data.map { preferences: Preferences ->
            preferences[TOKEN_KEY]?.let { encryptedToken ->
                try {
                    val encryptionKey = encryptionKeyManager.getOrCreateKey()
                    AesEncryption.decrypt(encryptedToken, encryptionKey)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun hasToken(): Boolean {
        val preferences: Preferences = dataStore.data.first()
        return preferences[TOKEN_KEY] != null
    }
}
