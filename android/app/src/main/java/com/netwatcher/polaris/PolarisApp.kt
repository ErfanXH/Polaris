package com.netwatcher.polaris

import android.app.Application
import com.netwatcher.polaris.di.TokenManager

class PolarisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this) // ✅ Best place for global singleton init
    }
}