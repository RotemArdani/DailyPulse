package com.colman.dailypulse

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.cloudinary.android.MediaManager
import com.colman.dailypulse.di.initKoin
import com.colman.dailypulse.workers.KoinWorkerFactory
import com.colman.dailypulse.workers.scheduleDailyReminder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.mp.KoinPlatform.getKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "dgia4gw4w"
        MediaManager.init(this, config)

        initKoin() {
            androidLogger()
            androidContext(this@MyApplication)
        }

        val workerFactory = KoinWorkerFactory(getKoin())
        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )

        scheduleDailyReminder(applicationContext)
    }
}