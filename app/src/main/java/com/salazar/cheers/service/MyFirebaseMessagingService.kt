package com.salazar.cheers.service

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.salazar.cheers.R
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.ui.theme.Purple200
import com.salazar.cheers.util.Utils.getCircledBitmap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL


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
            val avatar = remoteMessage.data["avatar"]

            Log.d("FCM", remoteMessage.notification.toString())
            makeNotification(remoteMessage.notification!!, avatar)
        }
        val intent = Intent("NewMessage")
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun makeNotification(
        remoteNotification: RemoteMessage.Notification,
        profilePictureUrl: String?,
    ) {
        val builder =
            NotificationCompat.Builder(
                this,
                getString(R.string.default_notification_channel_id)
            )
                .setSmallIcon(R.drawable.ic_cheers_logo)
                .setContentTitle(remoteNotification.title)
                .setColor(Purple200.toArgb())
                .setContentText(remoteNotification.body)
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (profilePictureUrl != null) {
            val url = URL(profilePictureUrl)
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream()).getCircledBitmap()
            builder.setLargeIcon(image)

            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        } else {
            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
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