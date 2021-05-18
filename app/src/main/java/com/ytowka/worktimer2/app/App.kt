package com.ytowka.worktimer2.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.ytowka.worktimer2.utils.C
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= 26){
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val stateChannel = NotificationChannel(
                C.NOTIFICATION_STATE_CHANNEL_ID,
                C.NOTIFICATION_STATE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val actionFinishedChannel = NotificationChannel(
                C.NOTIFICATION_ACTION_CHANNEL_ID,
                C.NOTIFICATION_ACTION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(actionFinishedChannel)
            notificationManager.createNotificationChannel(stateChannel)
        }

    }
}