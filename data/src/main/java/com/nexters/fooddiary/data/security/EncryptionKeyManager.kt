package com.nexters.fooddiary.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "encryption_key_prefs",
        Context.MODE_PRIVATE
    )
    
    private val KEY_ALIAS = "auth_token_encryption_key"

    fun getOrCreateKey(): SecretKey {
        val keyBytes = prefs.getString(KEY_ALIAS, null)?.let {
            Base64.decode(it, Base64.NO_WRAP)
        }
        
        return keyBytes?.takeIf { it.isNotEmpty() }?.let {
            AesEncryption.keyFromBytes(it)
        } ?: run {
            val newKey = AesEncryption.generateKey()
            val keyBytesString = Base64.encodeToString(
                AesEncryption.keyToBytes(newKey),
                Base64.NO_WRAP
            )
            prefs.edit().putString(KEY_ALIAS, keyBytesString).apply()
            newKey
        }
    }

    fun deleteKey() {
        prefs.edit().remove(KEY_ALIAS).apply()
    }
}
