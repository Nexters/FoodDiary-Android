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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val NICKNAME_KEY = stringPreferencesKey("user_nickname")

    private var cachedToken: String? = null
    private var cachedNickname: String? = null

    fun getCachedToken(): String? = cachedToken

    fun getCachedNickname(): String? = cachedNickname

    suspend fun initializeCache() {
        cachedToken = getToken()
        cachedNickname = getNickname()
    }

    suspend fun saveToken(token: String) {
        val encryptionKey = encryptionKeyManager.getOrCreateKey()
        val encryptedToken = AesEncryption.encrypt(token, encryptionKey)

        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = encryptedToken
        }

        cachedToken = token
    }

    suspend fun saveNickname(nickname: String) {
        dataStore.edit { preferences ->
            preferences[NICKNAME_KEY] = nickname
        }

        cachedNickname = nickname
    }

    suspend fun getToken(): String? = runCatching {
        dataStore.data.first()[TOKEN_KEY]?.let { encryptedToken ->
            val encryptionKey = encryptionKeyManager.getOrCreateKey()
            AesEncryption.decrypt(encryptedToken, encryptionKey)
        }
    }.getOrNull()

    suspend fun getNickname(): String? = runCatching {
        val nickname = dataStore.data.first()[NICKNAME_KEY]
        cachedNickname = nickname
        nickname
    }.getOrNull()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTokenFlow(): Flow<String?> = dataStore.data.flatMapLatest { preferences ->
        flow {
            val token = preferences[TOKEN_KEY]?.let { encryptedToken ->
                runCatching {
                    val encryptionKey = encryptionKeyManager.getOrCreateKey()
                    AesEncryption.decrypt(encryptedToken, encryptionKey)
                }.getOrNull()
            }
            emit(token)
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }

        cachedToken = null
    }

    suspend fun deleteNickname() {
        dataStore.edit { preferences ->
            preferences.remove(NICKNAME_KEY)
        }

        cachedNickname = null
    }

    suspend fun hasToken(): Boolean =
        dataStore.data.first()[TOKEN_KEY] != null
}
