package com.nexters.fooddiary.data.security

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = context.encryptionKeyDataStore
    
    companion object {
        private val Context.encryptionKeyDataStore: DataStore<Preferences> by preferencesDataStore(name = "encryption_key_prefs")
    }
    
    private val KEY_ALIAS = stringPreferencesKey("auth_token_encryption_key")

    suspend fun getOrCreateKey(): SecretKey {
        val preferences = dataStore.data.first()
        val keyBytesString = preferences[KEY_ALIAS]
        
        return keyBytesString
            ?.let { Base64.decode(it, Base64.NO_WRAP) }
            ?.takeIf { it.isNotEmpty() }
            ?.let { AesEncryption.keyFromBytes(it) }
            ?: createAndSaveKey()
    }

    private suspend fun createAndSaveKey(): SecretKey {
        val newKey = AesEncryption.generateKey()
        val keyBytesString = Base64.encodeToString(
            AesEncryption.keyToBytes(newKey),
            Base64.NO_WRAP
        )
        dataStore.edit { preferences ->
            preferences[KEY_ALIAS] = keyBytesString
        }
        return newKey
    }

    suspend fun deleteKey() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_ALIAS)
        }
    }
}
