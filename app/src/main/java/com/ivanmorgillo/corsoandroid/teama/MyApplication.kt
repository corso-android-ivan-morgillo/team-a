package com.ivanmorgillo.corsoandroid.teama

import android.app.Application
import android.os.StrictMode
import com.ivanmorgillo.corsoandroid.teama.crashlytics.CrashReportingTree
import com.ivanmorgillo.corsoandroid.teama.crashlytics.LineNumberDebugTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
class MyApplication : Application() {
    override fun onCreate() {
        setupStrictMode()
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
