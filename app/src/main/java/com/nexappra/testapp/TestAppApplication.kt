package com.nexappra.testapp

import android.app.Application
import com.nexappra.testapp.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TestAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createNotificationChannel(
            context = this
        )
    }
}