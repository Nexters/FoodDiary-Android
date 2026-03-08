package com.nexters.fooddiary.data.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object AesEncryption {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16

    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        keyGenerator.init(KEY_SIZE)
        return keyGenerator.generateKey()
    }

    fun keyFromBytes(keyBytes: ByteArray): SecretKey {
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun keyToBytes(key: SecretKey): ByteArray {
        return key.encoded
    }

    fun encrypt(plaintext: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        
        val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec)
        
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        
        val encryptedData = ByteArray(iv.size + ciphertext.size)
        System.arraycopy(iv, 0, encryptedData, 0, iv.size)
        System.arraycopy(ciphertext, 0, encryptedData, iv.size, ciphertext.size)
        
        return Base64.encodeToString(encryptedData, Base64.NO_WRAP)
    }

    fun decrypt(encryptedData: String, key: SecretKey): String {
        val encryptedBytes = Base64.decode(encryptedData, Base64.NO_WRAP)
        
        val iv = ByteArray(GCM_IV_LENGTH)
        System.arraycopy(encryptedBytes, 0, iv, 0, GCM_IV_LENGTH)
        
        val ciphertext = ByteArray(encryptedBytes.size - GCM_IV_LENGTH)
        System.arraycopy(encryptedBytes, GCM_IV_LENGTH, ciphertext, 0, ciphertext.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec)
        
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charsets.UTF_8)
    }
}

