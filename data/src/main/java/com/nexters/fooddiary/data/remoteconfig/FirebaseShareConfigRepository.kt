package com.nexters.fooddiary.data.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.nexters.fooddiary.domain.repository.ShareConfigRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FirebaseShareConfigRepository @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    @Named("shareBaseUrl") private val shareBaseUrl: String,
) : ShareConfigRepository {
    override fun getShareStoreLink(): String? {
        val isEnabled = remoteConfig.getBoolean(KEY_SHARE_STORE_LINK_ENABLED)
        if (!isEnabled) return null

        return shareBaseUrl
            .trim()
            .takeIf { it.isNotBlank() }
    }

    private companion object {
        const val KEY_SHARE_STORE_LINK_ENABLED = "use_store_link"
    }
}
