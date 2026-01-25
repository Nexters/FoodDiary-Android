package com.nexters.fooddiary.presentation.image

import android.app.Application
import com.airbnb.mvrx.Mavericks
import dagger.hilt.android.testing.CustomTestApplication

open class FoodClassifierTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
    }
}

@CustomTestApplication(FoodClassifierTestApplication::class)
interface TestApplicationEntryPoint

