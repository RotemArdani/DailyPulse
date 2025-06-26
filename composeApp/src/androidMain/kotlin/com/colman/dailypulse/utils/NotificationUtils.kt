package com.colman.dailypulse.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.colman.dailypulse.R

class NotificationUtils() {
    fun showHabitReminderNotification(context: Context) {
        val channelId = "habit_reminder_channel"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Don't forget your habits!")
            .setContentText("You have unfinished habits for today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()


        notificationManager.notify(1, notification)
    }
}