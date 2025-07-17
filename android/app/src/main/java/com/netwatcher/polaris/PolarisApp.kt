package com.netwatcher.polaris

import android.app.Application
import com.netwatcher.polaris.di.CookieManager

class PolarisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CookieManager.init(this) // ✅ Best place for global singleton init
    }
}