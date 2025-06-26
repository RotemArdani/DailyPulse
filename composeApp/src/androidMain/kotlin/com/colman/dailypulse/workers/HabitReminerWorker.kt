package com.colman.dailypulse.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.colman.dailypulse.data.habits.HabitsRepository
import com.colman.dailypulse.utils.NotificationUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.colman.dailypulse.data.Result as DailyPulseResult

class HabitReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val habitsRepository: HabitsRepository
) : CoroutineWorker(appContext, workerParams) {
    private val notificationUtils = NotificationUtils()

    override suspend fun doWork(): Result {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        return when (val result = habitsRepository.getHabits()) {
            is DailyPulseResult.Success -> {
                val habits = result.data?.items
                val hasUnfinished = habits?.any { habit ->
                    val lastDoneDate = habit.lastModified
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                    lastDoneDate != today
                }


                if (hasUnfinished == true) {
                    notificationUtils.showHabitReminderNotification(applicationContext)
                }

                Result.success()
            }

            is DailyPulseResult.Failure -> {
                Result.retry()
            }
        }
    }
}