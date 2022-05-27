package com.salazar.cheers.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.ui.theme.Purple200
import com.salazar.cheers.util.Utils.getCircledBitmap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(newRegistrationToken: String) {
        super.onNewToken(newRegistrationToken)

        if (FirebaseAuth.getInstance().currentUser == null)
            return

//        GlobalScope.launch {
//            userRepository.addTokenToNeo4j(newRegistrationToken)
//        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification

        if (notification != null) {
            val avatar = remoteMessage.data["avatar"]
            makeNotification(
                notification.title.toString(),
                notification.body.toString(),
                avatar
            )
        }

        Log.d("FCM", remoteMessage.toString())

        if (remoteMessage.data["type"] == "newMessage") {
            makeNotification(
                remoteMessage.data["title"].toString(),
                remoteMessage.data["body"].toString(),
                remoteMessage.data["avatar"],
                remoteMessage.data["channelId"].toString(),
            )
        }
        val intent = Intent("NewMessage")
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun makeNotification(
        title: String,
        body: String,
        profilePictureUrl: String?,
        channelId: String? = null,
    ) {
        val builder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_cheers_logo)
                .setContentTitle(title)
                .setColor(Purple200.toArgb())
                .setContentText(body)
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

        if (channelId != null) {
            val taskDetailIntent = Intent(
                Intent.ACTION_VIEW,
                "https://cheers-a275e.web.app/chat/${channelId}".toUri(),
                this,
                MainActivity::class.java
            )

            val pending: PendingIntent? = TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(taskDetailIntent)
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            builder.setContentIntent(pending)
        }

        if (profilePictureUrl != null && profilePictureUrl.isNotBlank()) {
            try {
                val url = URL(profilePictureUrl)
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    .getCircledBitmap()
                builder.setLargeIcon(image)
            } catch (e: Exception) {
                Log.e("FirebaseMessagingService", e.toString())
            }

            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        } else {
            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        }

    }

}