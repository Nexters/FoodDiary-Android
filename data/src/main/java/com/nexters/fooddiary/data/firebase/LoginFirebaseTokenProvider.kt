package com.nexters.fooddiary.data.firebase

import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface LoginFirebaseTokenProvider {
    suspend fun getDeviceToken(): String
    suspend fun getInstallationId(): String
}

@Singleton
class LoginFirebaseTokenProviderImpl @Inject constructor() : LoginFirebaseTokenProvider {
    override suspend fun getDeviceToken(): String {
        return runCatching {
            FirebaseMessaging.getInstance().token.await().orEmpty()
        }.getOrElse {
            ""
        }
    }

    override suspend fun getInstallationId(): String {
        return runCatching {
            FirebaseInstallations.getInstance().id.await().orEmpty()
        }.getOrElse {
            ""
        }
    }
}
