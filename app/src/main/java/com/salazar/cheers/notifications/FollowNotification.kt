package com.salazar.cheers.notifications

import android.app.Service
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.Purple200


fun Service.defaultNotification(
    title: String,
    body: String,
): NotificationCompat.Builder {
    return NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
        .setSmallIcon(R.drawable.ic_cheers_logo)
        .setContentTitle(title)
        .setColor(Purple200.toArgb())
        .setGroup(getString(R.string.general_group_id))
        .setContentText(body)
        .setChannelId(getString(R.string.default_notification_channel_id))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
}

fun Service.chatNotification(
    title: String,
    body: String,
): NotificationCompat.Builder {
    return NotificationCompat.Builder(this, getString(R.string.chat_notification_channel_id))
        .setSmallIcon(R.drawable.ic_cheers_logo)
        .setContentTitle(title)
        .setColor(Purple200.toArgb())
        .setGroup(getString(R.string.general_group_id))
        .setContentText(body)
        .setVibrate(longArrayOf(1000))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
}

fun Service.newPostNotification(
    title: String,
    body: String,
): NotificationCompat.Builder {
    return NotificationCompat.Builder(this, getString(R.string.new_post_notification_channel_id))
        .setSmallIcon(R.drawable.ic_cheers_logo)
        .setContentTitle(title)
        .setColor(Purple200.toArgb())
        .setGroup(getString(R.string.general_group_id))
        .setContentText(body)
        .setVibrate(longArrayOf(1000))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(R.drawable.ic_beer, "CHEERS", null)
        .setAutoCancel(true)
}

fun Service.newFollowerNotification(
    title: String,
    body: String,
): NotificationCompat.Builder {
    return NotificationCompat.Builder(this, getString(R.string.new_follower_notification_channel_id))
        .setSmallIcon(R.drawable.ic_cheers_logo)
        .setContentTitle(title)
        .setColor(Purple200.toArgb())
        .setVibrate(longArrayOf(1000))
        .setGroup(getString(R.string.general_group_id))
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
}