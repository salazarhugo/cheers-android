package com.salazar.cheers.service

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
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.notifications.chatNotification
import com.salazar.cheers.notifications.defaultNotification
import com.salazar.cheers.notifications.newFollowerNotification
import com.salazar.cheers.notifications.newPostNotification
import com.salazar.cheers.ui.MainActivity
import com.salazar.cheers.util.Utils.getCircledBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.net.URL
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseMessagingService @Inject constructor(
    private val userRepository: UserRepository,
) : FirebaseMessagingService() {

    override fun onNewToken(newRegistrationToken: String) {
        super.onNewToken(newRegistrationToken)

        if (FirebaseAuth.getInstance().currentUser == null)
            return

        runBlocking {
            userRepository.addTokenToNeo4j(newRegistrationToken)
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


        val pending = setIntent("https://cheers-a275e.web.app".toUri())
        builder.setContentIntent(pending)

        if (roomId != null) {
            val uri = "https://cheers-a275e.web.app/chat/${roomId}".toUri()
            val pending = setIntent(uri)
            builder.setContentIntent(pending)
        }

        if (avatar != null && avatar.isNotBlank()) {
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