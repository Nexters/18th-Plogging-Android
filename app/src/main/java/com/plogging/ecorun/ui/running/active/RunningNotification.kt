package com.plogging.ecorun.ui.running.active

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.plogging.ecorun.R

object RunningNotification {
    private const val CHANNEL_ID = "Plogging"
    private const val CHANNEL_NAME = "Eco Run"
    private const val CHANNEL_DESCRIPTION = "Eco Run channel"

    fun init(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                // Create the NotificationChannel
                val name = CHANNEL_NAME
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                mChannel.description = CHANNEL_DESCRIPTION
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }

    fun generateNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        return builder.setContentTitle("플로깅 중입니다!")
            .setSmallIcon(R.drawable.auth_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .build()
    }
}