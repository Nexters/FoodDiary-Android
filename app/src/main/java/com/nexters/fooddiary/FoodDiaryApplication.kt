package com.nexters.fooddiary

import android.app.Application
import com.nexters.fooddiary.core.common.di.commonModule
import com.nexters.fooddiary.data.di.networkModule
import com.nexters.fooddiary.data.di.dataModule
import com.nexters.fooddiary.domain.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FoodDiaryApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@FoodDiaryApplication)
            modules(
                // Core modules
                commonModule,
                networkModule,

                // Layer modules
                dataModule,
                domainModule,
            )
        }
    }
}
