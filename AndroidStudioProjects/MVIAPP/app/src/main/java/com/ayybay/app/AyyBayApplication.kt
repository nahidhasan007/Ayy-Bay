package com.ayybay.app

import android.app.Application
import com.ayybay.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AyyBayApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AyyBayApplication)
            modules(appModule)
        }
    }
}