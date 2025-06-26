package com.colman.dailypulse.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleDailyReminder(context: Context) {
    val now = Calendar.getInstance()
    val reminderTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 17)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
    }

    val initialDelay = reminderTime.timeInMillis - now.timeInMillis

    val request = PeriodicWorkRequestBuilder<HabitReminderWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()


    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "dailyHabitReminder",
        ExistingPeriodicWorkPolicy.UPDATE,
        request
    )
}