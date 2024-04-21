package com.salazar.cheers.core.data.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.salazar.cheers.R
import com.salazar.cheers.core.data.notifications.chatNotification
import com.salazar.cheers.core.data.notifications.defaultNotification
import com.salazar.cheers.core.data.notifications.newFollowerNotification
import com.salazar.cheers.core.data.notifications.newPostNotification
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.core.util.Utils.getCircledBitmap
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.net.URL
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var userRepositoryImpl: UserRepositoryImpl

    override fun onNewToken(newRegistrationToken: String) {
        super.onNewToken(newRegistrationToken)

        if (FirebaseAuth.getInstance().currentUser == null)
            return

        runBlocking {
            userRepositoryImpl.addTokenToNeo4j(newRegistrationToken)
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", remoteMessage.data.toString())

        val notification = remoteMessage.notification

        if (notification != null) {
            makeNotification(
                title = notification.title.toString(),
                body = notification.body.toString(),
                avatar = remoteMessage.data["avatar"],
                channelId = "DEFAULT",
            )
            return
        }

        makeNotification(
            title = remoteMessage.data["title"].toString(),
            body = remoteMessage.data["body"].toString(),
            avatar = remoteMessage.data["avatar"],
            channelId = remoteMessage.data["channelId"].toString(),
            roomId = remoteMessage.data["roomId"],
        )
    }

    @SuppressLint("MissingPermission")
    private fun makeNotification(
        title: String,
        body: String,
        avatar: String?,
        channelId: String,
        roomId: String? = null,
    ) {
        val builder = when (channelId) {
            getString(R.string.default_notification_channel_id) -> defaultNotification(title, body)
            getString(R.string.new_follower_notification_channel_id) -> newFollowerNotification(
                title,
                body
            )
            getString(R.string.new_post_notification_channel_id) -> newPostNotification(title, body)
            getString(R.string.chat_notification_channel_id) -> {
//                if (!body.contains("is typing..."))
//                    incrementPreference(unreadChatCount)
                chatNotification(title, body)
            }
            else -> {
//                incrementPreference(activityCount)
                defaultNotification(title, body)
            }
        }


        val pending = setIntent(Constants.URI.toUri())
        builder.setContentIntent(pending)

        if (roomId != null) {
            val uri = "${Constants.URI}/chat/${roomId}".toUri()
            val pending = setIntent(uri)
            builder.setContentIntent(pending)
        }

        if (!avatar.isNullOrBlank()) {
            try {
                val url = URL(avatar)
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

    private fun setIntent(url: Uri): PendingIntent? {
        val taskDetailIntent = Intent(
            Intent.ACTION_VIEW,
            url,
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

        return pending
    }
}