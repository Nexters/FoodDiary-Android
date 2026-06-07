package com.nexters.fooddiary

import android.app.Application
import android.util.Log
import com.airbnb.mvrx.Mavericks
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import javax.inject.Inject

@HiltAndroidApp
class FoodDiaryApplication : Application() {
    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
        remoteConfig.fetchAndActivate()
        initSentry()
    }

    private fun initSentry() {
        BuildConfig.SENTRY_DSN
            .takeIf { it.isNotBlank() }
            ?.let { dsn ->
                SentryAndroid.init(this) { options ->
                    options.dsn = dsn
                    options.environment = BuildConfig.BUILD_TYPE
                    options.isEnableAutoSessionTracking = true
                    options.isAttachStacktrace = true
                    options.isDebug = BuildConfig.DEBUG
                }
            }
            ?: Log.w("Sentry", "DSN 미설정. local.properties에 sentry.dsn 추가 후 Clean + Rebuild 필요.")
    }

}
