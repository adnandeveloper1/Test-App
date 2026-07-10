package com.nexappra.testapp.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexappra.testapp.R

class AppFirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(
            TAG,
            "New FCM token: $token"
        )

        /*
         * In production, send this updated token to your
         * secure backend or Firestore user document.
         *
         * Do not assume that an FCM token remains unchanged.
         */
    }

    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {
        super.onMessageReceived(remoteMessage)

        Log.d(
            TAG,
            "Message received from: ${remoteMessage.from}"
        )

        Log.d(
            TAG,
            "Message data: ${remoteMessage.data}"
        )

        val title =
            remoteMessage.notification?.title
                ?: remoteMessage.data["title"]
                ?: getString(R.string.app_name)

        val body =
            remoteMessage.notification?.body
                ?: remoteMessage.data["body"]
                ?: getString(
                    R.string.default_notification_body
                )

        NotificationHelper.showNotification(
            context = this,
            title = title,
            body = body
        )
    }

    companion object {
        private const val TAG = "AppFCMService"
    }
}