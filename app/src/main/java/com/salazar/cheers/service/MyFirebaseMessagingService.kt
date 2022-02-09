package com.salazar.cheers.service

import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.salazar.cheers.R
import com.salazar.cheers.backend.Neo4jUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(newRegistrationToken: String) {
        super.onNewToken(newRegistrationToken)

        if (FirebaseAuth.getInstance().currentUser == null)
            return

        GlobalScope.launch {
            addTokenToNeo4j(newRegistrationToken)
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            Log.d("FCM", remoteMessage.notification?.imageUrl.toString())
            makeNotification(remoteMessage.notification!!)
        }
        val intent = Intent("NewMessage")
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun makeNotification(remoteNotification: RemoteMessage.Notification) {
        val builder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(remoteNotification.title)
                .setContentText(remoteNotification.body)
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    companion object {
        suspend fun addTokenToNeo4j(newRegistrationToken: String?) {
            if (newRegistrationToken == null)
                throw NullPointerException("FCM token is null.")

            Neo4jUtil.addRegistrationToken(newRegistrationToken)
        }
    }
}