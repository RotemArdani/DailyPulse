package com.colman.dailypulse.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class KoinWorkerFactory(
    private val koin: org.koin.core.Koin
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            HabitReminderWorker::class.qualifiedName ->
                HabitReminderWorker(appContext, workerParameters, koin.get())
            else -> null
        }
    }
}