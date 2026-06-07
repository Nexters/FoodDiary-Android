package com.nexters.fooddiary.di

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.nexters.fooddiary.BuildConfig
import com.nexters.fooddiary.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Named("isDebug")
    fun provideIsDebug(): Boolean = BuildConfig.DEBUG

    @Provides
    @Named("useMockApi")
    fun provideUseMockApi(): Boolean = BuildConfig.USE_MOCK_API

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return Firebase.remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = DEFAULT_REMOTE_CONFIG_FETCH_INTERVAL
                }
            )
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    private const val DEFAULT_REMOTE_CONFIG_FETCH_INTERVAL = 3600L
}
